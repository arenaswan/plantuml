/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 *
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 *
 *
 */
package net.sourceforge.plantuml.activitydiagram3.command;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.color.ColorParser;

public class CommandIf4 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandIf4() {
		super(getRegexConcat());
	}

	static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"), //
				ColorParser.exp4(), //
				new RegexLeaf("if"), //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("TEST", "\\((.*?)\\)"), //
				new RegexLeaf("[%s]*(is|equals?)[%s]*"), //
				new RegexLeaf("WHEN", "\\((.+?)\\)"), //
				new RegexLeaf("[%s]*then[%s]*"), //
				new RegexLeaf(";?$"));
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, RegexResult arg) {
		final HtmlColor color = diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("COLOR", 0));

		String test = arg.get("TEST", 0);
		if (test.length() == 0) {
			test = null;
		}
		diagram.startIf(Display.getWithNewlines(test), Display.getWithNewlines(arg.get("WHEN", 0)), color);

		return CommandExecutionResult.ok();
	}
}
