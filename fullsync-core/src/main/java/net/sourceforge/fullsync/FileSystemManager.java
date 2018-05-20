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
package net.sourceforge.fullsync;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.sourceforge.fullsync.fs.FileSystem;
import net.sourceforge.fullsync.fs.Site;
import net.sourceforge.fullsync.fs.buffering.BufferingProvider;
import net.sourceforge.fullsync.fs.buffering.syncfiles.SyncFilesBufferingProvider;
import net.sourceforge.fullsync.fs.filesystems.FTPFileSystem;
import net.sourceforge.fullsync.fs.filesystems.LocalFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SFTPFileSystem;
import net.sourceforge.fullsync.fs.filesystems.SmbFileSystem;

public class FileSystemManager {
	public static final String BUFFER_STRATEGY_SYNCFILES = "syncfiles"; //$NON-NLS-1$

	private FileSystem getFilesystem(final String scheme) {
		switch (scheme) {
			case "file":
				return new LocalFileSystem();
			case "ftp":
				return new FTPFileSystem();
			case "sftp":
				return new SFTPFileSystem();
			case "smb":
				return new SmbFileSystem();
			default:
				return null;
		}
	}

	public final Site createConnection(final FullSync fullsync, final ConnectionDescription desc, boolean isInteractive)
		throws FileSystemException, IOException, URISyntaxException {
		URI url = desc.getUri();
		String scheme = url.getScheme();

		FileSystem fs = getFilesystem(scheme);

		Site s = null;
		if (null != fs) {
			s = fs.createConnection(fullsync, desc, isInteractive);
			/* FIXME: [BUFFERING] uncomment to reenable buffering
			String bufferStrategy = desc.getParameter(ConnectionDescription.PARAMETER_BUFFER_STRATEGY);
			if ((null != bufferStrategy) && !"".equals(bufferStrategy)) {
				s = resolveBuffering(s, bufferStrategy);
			}
			*/
		}
		return s;
	}

	public Site resolveBuffering(final Site dir, final String bufferStrategy) throws FileSystemException, IOException {
		BufferingProvider p = null;
		if (BUFFER_STRATEGY_SYNCFILES.equals(bufferStrategy)) {
			p = new SyncFilesBufferingProvider();
		}

		if (null == p) {
			throw new FileSystemException("BufferStrategy '" + bufferStrategy + "' not found");
		}

		return p.createBufferedSite(dir);
	}
}
