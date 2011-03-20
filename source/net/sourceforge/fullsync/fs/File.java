/**
 *	@license
 *	This program is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU General Public License
 *	as published by the Free Software Foundation; either version 2
 *	of the License, or (at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *	Boston, MA  02110-1301, USA.
 *
 *	---
 *	@copyright Copyright (C) 2005, Jan Kopcsek <codewright@gmx.net>
 *	@copyright Copyright (C) 2011, Obexer Christoph <cobexer@gmail.com>
 */
/*
 * Created on 18.07.2004
 */
package net.sourceforge.fullsync.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public interface File extends Serializable
{
    public String getName();
    public String getPath();
    public File getParent();
    public boolean isDirectory();
    public boolean isFile();
    public boolean exists();
    public boolean isFiltered();
    public void setFiltered( boolean filtered );

    public boolean isBuffered();
    public File getUnbuffered() throws IOException;
    public void refreshBuffer() throws IOException;

    public void writeFileAttributes() throws IOException;
    public void setFileAttributes( FileAttributes att );
    public FileAttributes getFileAttributes();

    public Collection<File> getChildren() throws IOException;
    public File getChild( String name ) throws IOException;

    // TODO currently, 'create' isnt the right word
    //		they do not exist before and may not exists after sync
    public File createChild( String name, boolean directory ) throws IOException;

    public void refresh() throws IOException;

    public boolean makeDirectory()  throws IOException;

    public InputStream getInputStream() throws IOException;
    public OutputStream getOutputStream() throws IOException;

    public boolean delete() throws IOException;

    @Override
	public String toString();
}
