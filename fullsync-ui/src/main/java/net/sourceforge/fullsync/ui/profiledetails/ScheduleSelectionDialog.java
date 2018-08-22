/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 * For information about the authors of this project Have a look
 * at the AUTHORS file in the root of this project.
 */
package net.sourceforge.fullsync.ui.profiledetails;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.schedule.Schedule;
import net.sourceforge.fullsync.ui.ImageRepository;
import net.sourceforge.fullsync.ui.Messages;
import net.sourceforge.fullsync.ui.UISettings;
import net.sourceforge.fullsync.ui.schedule.CrontabScheduleOptions;
import net.sourceforge.fullsync.ui.schedule.IntervalScheduleOptions;
import net.sourceforge.fullsync.ui.schedule.NullScheduleOptions;
import net.sourceforge.fullsync.ui.schedule.ScheduleOptions;

class ScheduleSelectionDialog {
	private final ImageRepository imageRepository;
	private Group groupOptions;
	private Combo cbType;

	private Schedule schedule;

	@Inject
	public ScheduleSelectionDialog(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

	void open(Shell parent) {
		try {
			Shell dialogShell = new Shell(parent, SWT.PRIMARY_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE);
			dialogShell.setText(Messages.getString("ScheduleSelectionDialog.EditScheduling")); //$NON-NLS-1$
			dialogShell.setImage(imageRepository.getImage("Scheduler_Icon.png")); //$NON-NLS-1$
			GridLayout thisLayout = new GridLayout();
			thisLayout.numColumns = 2;
			dialogShell.setLayout(thisLayout);

			// schedule type
			Composite compositeTop = new Composite(dialogShell, SWT.NONE);
			GridLayout compositeTopLayout = new GridLayout();
			GridData compositeTopLData = new GridData();
			compositeTopLData.horizontalSpan = 2;
			compositeTopLData.horizontalAlignment = SWT.FILL;
			compositeTop.setLayoutData(compositeTopLData);
			compositeTopLayout.numColumns = 2;
			compositeTop.setLayout(compositeTopLayout);

			Label labelScheduleType = new Label(compositeTop, SWT.NONE);
			labelScheduleType.setText(Messages.getString("ScheduleSelectionDialog.SchedulingType")); //$NON-NLS-1$
			GridData labelScheduleTypeLData = new GridData();
			labelScheduleType.setLayoutData(labelScheduleTypeLData);

			cbType = new Combo(compositeTop, SWT.DROP_DOWN | SWT.READ_ONLY);
			GridData cbTypeLData = new GridData();
			cbTypeLData.horizontalAlignment = SWT.FILL;
			cbTypeLData.grabExcessHorizontalSpace = true;
			cbType.setLayoutData(cbTypeLData);
			cbType.addListener(SWT.Modify, e -> {
				Control[] children = groupOptions.getChildren();
				if ((cbType.getSelectionIndex() > -1) && (cbType.getSelectionIndex() < children.length)) {
					Control c = children[cbType.getSelectionIndex()];
					((StackLayout) groupOptions.getLayout()).topControl = c;
					groupOptions.layout();
				}
			});

			// scheduling options
			groupOptions = new Group(dialogShell, SWT.FILL);
			StackLayout groupOptionsLayout = new StackLayout();
			GridData groupOptionsLData = new GridData();
			groupOptionsLData.grabExcessVerticalSpace = true;
			groupOptionsLData.grabExcessHorizontalSpace = true;
			groupOptionsLData.horizontalAlignment = SWT.FILL;
			groupOptionsLData.verticalAlignment = SWT.FILL;
			groupOptionsLData.horizontalSpan = 2;
			groupOptions.setLayoutData(groupOptionsLData);
			groupOptions.setLayout(groupOptionsLayout);
			groupOptions.setText(Messages.getString("ScheduleSelectionDialog.Options")); //$NON-NLS-1$

			// dialog buttons
			Button buttonOk = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			buttonOk.setText(Messages.getString("ScheduleSelectionDialog.Ok")); //$NON-NLS-1$
			GridData buttonOkLData = new GridData();
			buttonOkLData.horizontalAlignment = SWT.END;
			buttonOkLData.grabExcessHorizontalSpace = true;
			buttonOkLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonOkLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonOk.setLayoutData(buttonOkLData);
			buttonOk.addListener(SWT.Selection, e -> {
				try {
					schedule = ((ScheduleOptions) ((StackLayout) groupOptions.getLayout()).topControl).getSchedule();
					dialogShell.dispose();
				}
				catch (Exception ex) {
					ExceptionHandler.reportException(ex);
				}
			});

			// cancel button
			Button buttonCancel = new Button(dialogShell, SWT.PUSH | SWT.CENTER);
			buttonCancel.setText(Messages.getString("ScheduleSelectionDialog.Cancel")); //$NON-NLS-1$
			GridData buttonCancelLData = new GridData();
			buttonCancelLData.heightHint = UISettings.BUTTON_HEIGHT;
			buttonCancelLData.widthHint = UISettings.BUTTON_WIDTH;
			buttonCancel.setLayoutData(buttonCancelLData);
			buttonCancel.addListener(SWT.Selection, e -> dialogShell.dispose());

			addScheduleOptions(new NullScheduleOptions(groupOptions));
			cbType.select(0);
			addScheduleOptions(new IntervalScheduleOptions(groupOptions));
			addScheduleOptions(new CrontabScheduleOptions(groupOptions));

			Display display = dialogShell.getDisplay();
			dialogShell.setSize(350, 350);

			Rectangle rect = parent.getBounds();
			Point dialogSize = dialogShell.getSize();
			int x = (rect.x + (rect.width / 2)) - (dialogSize.x / 2);
			int y = (rect.y + (rect.height / 2)) - (dialogSize.y / 2);
			dialogShell.setLocation(x, y);
			dialogShell.layout();
			dialogShell.open();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		catch (Exception e) {
			ExceptionHandler.reportException(e);
		}
	}

	private void addScheduleOptions(ScheduleOptions options) {
		cbType.add(options.getSchedulingName());
		if (options.canHandleSchedule(schedule)) {
			cbType.setText(options.getSchedulingName());
			options.setSchedule(schedule);
		}
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}
}
