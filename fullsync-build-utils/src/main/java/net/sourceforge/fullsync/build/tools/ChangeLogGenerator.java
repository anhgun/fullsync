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
package net.sourceforge.fullsync.build.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.sourceforge.fullsync.changelog.ChangeLogEntry;
import net.sourceforge.fullsync.changelog.ChangeLogLoader;

public class ChangeLogGenerator {
	private static void usage() {
		System.out.println("Usage: [--src-dir source-directory] [--pattern file-pattern] [--changelog output-file]");
		System.out.println("  --src-dir ..... directory in which to look for source files");
		System.out.println("  --pattern ..... pattern of files to consider for the changelog generation");
		System.out.println("  --changelog ... name of the target file for the changelog");
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ParseException {
		String srcDir = ".";
		String pattern = ".+\\.html";
		String changelog = null;

		for (int i = 0; (i + 1) < args.length; i += 2) {
			switch (args[i]) {
				case "--src-dir":
					srcDir = args[i + 1];
					break;
				case "--pattern":
					pattern = args[i + 1];
					break;
				case "--changelog":
					changelog = args[i + 1];
					break;
				default:
					System.err.println(String.format("Error: unknown argument '%s'", args[i]));
					usage();
					System.exit(1);
					break;
			}
		}
		if ((args.length > 0) && ((args.length % 2) > 0)) {
			System.err.println(String.format("Error: missing parameter for argument '%s'", args[args.length - 1]));
			usage();
			System.exit(1);
		}

		File dir = new File(srcDir);
		if (!dir.exists()) {
			System.err.println(String.format("Error: Directory '%s' does not exist.", dir.getAbsolutePath()));
			System.exit(1);
		}
		ChangeLogLoader c = new ChangeLogLoader();
		List<ChangeLogEntry> changelogEntries = c.load(dir, pattern);
		PrintWriter pw = null == changelog ? new PrintWriter(System.out) : new PrintWriter(changelog);
		for (ChangeLogEntry entry : changelogEntries) {
			entry.write("FullSync %s %s", " - %s", pw, DateTimeFormatter.ofPattern("MMMM d, uuuu"));
		}
		pw.flush();
	}
}
