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

import org.apache.commons.cli.CommandLine;

import com.google.inject.AbstractModule;

import net.sourceforge.fullsync.Preferences;
import net.sourceforge.fullsync.ProfileManager;
import net.sourceforge.fullsync.RuntimeConfiguration;
import net.sourceforge.fullsync.TaskGenerator;
import net.sourceforge.fullsync.cli.CliRuntimeConfiguration;

public class FullSyncModule extends AbstractModule {
	private final CommandLine line;
	private final String prefrencesFile;
	private final String profilesFile;

	public FullSyncModule(CommandLine line, String prefrencesFile, String profilesFile) {
		this.line = line;
		this.prefrencesFile = prefrencesFile;
		this.profilesFile = profilesFile;
	}

	@Override
	protected void configure() {
		bind(TaskGenerator.class).to(TaskGeneratorImpl.class);
		bind(Preferences.class).toInstance(new ConfigurationPreferences(prefrencesFile));
		bind(RuntimeConfiguration.class).toInstance(new CliRuntimeConfiguration(line));
		bind(ProfileManager.class).toInstance(new XmlBackedProfileManager(profilesFile));
	}
}