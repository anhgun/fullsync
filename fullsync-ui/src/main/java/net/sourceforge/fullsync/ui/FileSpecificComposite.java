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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.widgets.DirectoryDialog;

import net.sourceforge.fullsync.ConnectionDescription;
import net.sourceforge.fullsync.ConnectionDescription.Builder;

class FileSpecificComposite extends ProtocolSpecificComposite {
	@Override
	public void setConnectionDescription(ConnectionDescription connection) {
		super.setConnectionDescription(connection);
		if (null != connection) {
			textPath.setText(connection.getDisplayPath());
		}
		else {
			textPath.setText("");
		}
	}

	@Override
	public Builder getConnectionDescription() throws URISyntaxException {
		Builder builder = super.getConnectionDescription();
		builder.setUri(getURI());
		return builder;
	}

	protected URI getURI() {
		return new File(textPath.getText()).toURI();
	}

	@Override
	public void onBrowse() {
		ConnectionDescription desc = null;
		try {
			desc = getConnectionDescription().build();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		DirectoryDialog d = new DirectoryDialog(m_parent.getShell());
		if (null != desc) {
			d.setFilterPath(desc.getUri().getPath());
		}
		String dir = d.open();
		if (null != dir) {
			File f = new File(dir);
			try {
				setPath(f.getCanonicalPath());
			}
			catch (IOException e) {
				setPath("");
				e.printStackTrace();
			}
		}
	}
}
