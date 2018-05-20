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
package net.sourceforge.fullsync.ui;

import static org.eclipse.swt.events.SelectionListener.widgetDefaultSelectedAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.fullsync.ConnectionDescription;

class SftpSpecificComposite extends ProtocolSpecificComposite {
	private static final int DEFAULT_SFTP_PORT = 22;

	private Label labelHost;
	private Text textHost;
	private Label labelPort;
	private Spinner spinnerPort;
	private Label labelUsername;
	private Text textUsername;
	private Label labelPassword;
	private Text textPassword;
	private Button buttonKeybased;
	private Label labelKeyPassphrase;
	private Text textKeyPassphrase;

	@Override
	public void createGUI(final Composite parent) {
		m_parent = parent;

		GridData gridData1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData1.horizontalSpan = 2;

		// hostname
		labelHost = new Label(m_parent, SWT.NONE);
		labelHost.setText(Messages.getString("ProtocolSpecificComposite.Host"));
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.CENTER;
		textHost = new Text(m_parent, SWT.BORDER);
		GridData gridData3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData3.horizontalSpan = 2;
		textHost.setLayoutData(gridData3);

		labelPort = new Label(parent, SWT.NONE);
		labelPort.setText("Port:");
		spinnerPort = new Spinner(parent, SWT.BORDER);
		spinnerPort.setMinimum(1);
		spinnerPort.setMaximum(0xFFFF);
		spinnerPort.setSelection(DEFAULT_SFTP_PORT);
		GridData gridData4 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData4.horizontalSpan = 2;
		spinnerPort.setLayoutData(gridData4);

		labelUsername = new Label(m_parent, SWT.NONE);
		labelUsername.setText(Messages.getString("ProtocolSpecificComposite.Username"));
		textUsername = new Text(m_parent, SWT.BORDER);
		GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData2.horizontalSpan = 2;
		textUsername.setLayoutData(gridData2);
		labelPassword = new Label(m_parent, SWT.NONE);
		labelPassword.setText(Messages.getString("ProtocolSpecificComposite.Password"));
		textPassword = new Text(m_parent, SWT.BORDER);
		textPassword.setLayoutData(gridData1);
		textPassword.setEchoChar('*');

		buttonKeybased = new Button(m_parent, SWT.CHECK);
		buttonKeybased.setText(Messages.getString("ProtocolSpecificComposite.Keybased"));
		GridData radioKeybasedData = new GridData();
		radioKeybasedData.horizontalSpan = 3;
		buttonKeybased.setLayoutData(radioKeybasedData);
		Consumer<SelectionEvent> keybasedSelectionListener = e -> {
			boolean enabled = buttonKeybased.getSelection();
			labelKeyPassphrase.setEnabled(enabled);
			textKeyPassphrase.setEnabled(enabled);
		};

		buttonKeybased.addSelectionListener(widgetDefaultSelectedAdapter(keybasedSelectionListener));
		buttonKeybased.addSelectionListener(widgetSelectedAdapter(keybasedSelectionListener));

		labelKeyPassphrase = new Label(m_parent, SWT.NONE);
		labelKeyPassphrase.setText(Messages.getString("ProtocolSpecificComposite.KeyPassphrase"));
		textKeyPassphrase = new Text(m_parent, SWT.BORDER);
		textKeyPassphrase.setEchoChar('*');
		GridData textKeyPassphraseData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		textKeyPassphraseData.horizontalSpan = 2;
		textKeyPassphrase.setLayoutData(textKeyPassphraseData);
		super.createGUI(parent);
	}

	@Override
	public ConnectionDescription.Builder getConnectionDescription() throws URISyntaxException {
		ConnectionDescription.Builder builder = super.getConnectionDescription();
		String path = textPath.getText();
		if ((null == path) || path.isEmpty()) {
			path = "/";
		}

		URI uri = new URI(m_scheme, null, textHost.getText(), spinnerPort.getSelection(), path, null, null);
		builder.setUri(uri);
		builder.setUsername(textUsername.getText());
		builder.setPassword(textPassword.getText());
		builder.setPublicKeyAuth(buttonKeybased.getSelection());
		builder.setKeyPassphrase(textKeyPassphrase.getText());
		return builder;
	}

	@Override
	public void setConnectionDescription(final ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		URI uri = connection.getUri();
		textHost.setText(uri.getHost());
		int port = uri.getPort();
		if (-1 == port) {
			port = DEFAULT_SFTP_PORT;
		}
		spinnerPort.setSelection(port);
		textUsername.setText(connection.getUsername());
		textPassword.setText(connection.getPassword());
		boolean keybased = connection.getPublicKeyAuth().orElse(Boolean.FALSE).booleanValue();
		buttonKeybased.setSelection(keybased);
		labelKeyPassphrase.setEnabled(keybased);
		textKeyPassphrase.setEnabled(keybased);
		textKeyPassphrase.setText(connection.getKeyPassphrase().orElse(""));
	}

	@Override
	public void reset(final String scheme) {
		super.reset(scheme);
		textHost.setText("");
		spinnerPort.setSelection(DEFAULT_SFTP_PORT);
		textUsername.setText("");
		textPassword.setText("");
		buttonKeybased.setSelection(false);
		textKeyPassphrase.setText("");
		textKeyPassphrase.setEnabled(false);
		labelKeyPassphrase.setEnabled(false);
	}
}
