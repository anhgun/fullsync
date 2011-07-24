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
/*
 * Created on Nov 5, 2004
 */
package net.sourceforge.fullsync.impl;

import net.sourceforge.fullsync.RuleSet;
import net.sourceforge.fullsync.RuleSetDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michele Aiello
 */
public class AdvancedRuleSetDescriptor extends RuleSetDescriptor {

	private String ruleSetName;

	public AdvancedRuleSetDescriptor() {

	}

	public AdvancedRuleSetDescriptor(String ruleSetName) {
		this.ruleSetName = ruleSetName;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#getType()
	 */
	public String getType() {
		return "advanced";
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#serialize(org.w3c.dom.Document)
	 */
	public Element serialize(Document document) {
		Element advancedRuleSetElement = document.createElement("AdvancedRuleSet");
		advancedRuleSetElement.setAttribute("name", getRuleSetName());
		return advancedRuleSetElement;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#unserializeDescriptor(org.w3c.dom.Element)
	 */
	protected void unserializeDescriptor(Element element) {
		Element ruleSetNameElement = (Element) element.getElementsByTagName("AdvancedRuleSet").item(0);
		ruleSetName = ruleSetNameElement.getAttribute("name");
	}

	/**
	 * @see net.sourceforge.fullsync.RuleSetDescriptor#createRuleSet()
	 */
	public RuleSet createRuleSet() {
		SyncRules rules = new SyncRules(ruleSetName);
		rules.setJustLogging(false);

		return rules;
	}

	/**
	 * @return Returns the ruleSetName.
	 */
	public String getRuleSetName() {
		return ruleSetName;
	}

}
