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
package net.sourceforge.fullsync.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.fullsync.ExceptionHandler;
import net.sourceforge.fullsync.buffer.EntryDescriptor;
import net.sourceforge.fullsync.fs.File;

/**
 * @author <a href="mailto:codewright@gmx.net">Jan Kopcsek</a>
 */
public class DeleteNodeEntryDescriptor implements EntryDescriptor {
	private Object reference;
	private File node;

	public DeleteNodeEntryDescriptor(Object reference, File node) {
		this.reference = reference;
		this.node = node;
	}

	public Object getReferenceObject() {
		return reference;
	}

	public long getLength() {
		return 0;
	}

	public InputStream getInputStream() throws IOException {
		return null;
	}

	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	public void finishStore() {
	}

	public void finishWrite() {
		try {
			node.delete();

		}
		catch (IOException ioe) {
			ExceptionHandler.reportException(ioe);
		}
	}

	public String getOperationDescription() {
		return "Deleted File " + node.getPath();
	}

}
