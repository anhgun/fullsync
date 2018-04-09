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
package net.sourceforge.fullsync.ui.filterrule;

import javax.inject.Provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.rules.filefilter.FileFilter;
import net.sourceforge.fullsync.rules.filefilter.values.FilterValue;
import net.sourceforge.fullsync.rules.filefilter.values.OperandValue;
import net.sourceforge.fullsync.ui.FileFilterPage;

class SubfilterRuleComposite extends RuleComposite {
	private Button buttonFilter;
	private FilterValue value;

	SubfilterRuleComposite(Provider<FileFilterPage> fileFilterPageProvider, Composite parent, final FilterValue initialValue) {
		super(parent);
		value = initialValue;
		this.setLayout(new GridLayout(4, true));
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		this.setLayoutData(layoutData);

		textValue = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData textValueData = new GridData(SWT.FILL, SWT.FILL, true, true);
		textValueData.horizontalSpan = 3;
		textValue.setLayoutData(textValueData);
		if (null != initialValue) {
			textValue.setText(initialValue.toString());
		}
		textValue.setEditable(false);

		buttonFilter = new Button(this, SWT.PUSH);
		buttonFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttonFilter.setText("Set Filter...");
		buttonFilter.addListener(SWT.Selection, evt -> {
			try {
				FileFilterPage dialog = fileFilterPageProvider.get();
				dialog.setParent(getShell());
				dialog.setFileFilter(value.getValue());
				dialog.show();
				FileFilter newfilter = dialog.getFileFilter();
				if (null != newfilter) {
					value.setValue(newfilter);
					textValue.setText(value.toString());
					textValue.setToolTipText(value.toString());
				}
			}
			catch (Exception e) {
				ExceptionHandler.reportException(e);
			}
		});
	}

	@Override
	public OperandValue getValue() {
		return value;
	}
}
