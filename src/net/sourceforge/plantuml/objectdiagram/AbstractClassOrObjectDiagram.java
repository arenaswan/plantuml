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
package net.sourceforge.plantuml.objectdiagram;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.NoteLinkStrategy;
import net.sourceforge.plantuml.utils.UniqueSequence;

public abstract class AbstractClassOrObjectDiagram extends AbstractEntityDiagram {

	final public boolean insertBetween(IEntity entity1, IEntity entity2, IEntity node) {
		final Link link = foundLink(entity1, entity2);
		if (link == null) {
			return false;
		}
		final Link l1 = new Link(entity1, node, link.getType(), link.getLabel(), link.getLength(),
				link.getQualifier1(), null, link.getLabeldistance(), link.getLabelangle());
		final Link l2 = new Link(node, entity2, link.getType(), link.getLabel(), link.getLength(), null,
				link.getQualifier2(), link.getLabeldistance(), link.getLabelangle());
		addLink(l1);
		addLink(l2);
		removeLink(link);
		return true;
	}

	private Link foundLink(IEntity entity1, IEntity entity2) {
		final List<Link> links = getLinks();
		for (int i = links.size() - 1; i >= 0; i--) {
			final Link l = links.get(i);
			if (l.isBetween(entity1, entity2)) {
				return l;
			}
		}
		return null;
	}

	public int getNbOfHozizontalLollipop(IEntity entity) {
		if (entity.getLeafType() == LeafType.LOLLIPOP) {
			throw new IllegalArgumentException();
		}
		int result = 0;
		for (Link link : getLinks()) {
			if (link.getLength() == 1 && link.contains(entity) && link.containsType(LeafType.LOLLIPOP)) {
				result++;
			}

		}
		return result;
	}

	private final List<Association> associations = new ArrayList<Association>();

	public CommandExecutionResult associationClass(Code clName1A, Code clName1B, Code clName2A, Code clName2B,
			LinkType linkType, Display label) {
		final IEntity entity1A = getOrCreateLeaf(clName1A, null, null);
		final IEntity entity1B = getOrCreateLeaf(clName1B, null, null);
		final IEntity entity2A = getOrCreateLeaf(clName2A, null, null);
		final IEntity entity2B = getOrCreateLeaf(clName2B, null, null);
		final List<Association> same1 = getExistingAssociatedPoints(entity1A, entity1B);
		final List<Association> same2 = getExistingAssociatedPoints(entity2A, entity2B);
		if (same1.size() == 0 && same2.size() == 0) {

			final IEntity point1 = getOrCreateLeaf(UniqueSequence.getCode("apoint"), LeafType.POINT_FOR_ASSOCIATION,
					null);
			final IEntity point2 = getOrCreateLeaf(UniqueSequence.getCode("apoint"), LeafType.POINT_FOR_ASSOCIATION,
					null);

			insertPointBetween(entity1A, entity1B, point1);
			insertPointBetween(entity2A, entity2B, point2);

			final int length = 1;
			final Link point1ToPoint2 = new Link(point1, point2, linkType, label, length);
			addLink(point1ToPoint2);

			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot link two associations points");
	}

	private void insertPointBetween(final IEntity entity1A, final IEntity entity1B, final IEntity point1) {
		Link existingLink1 = foundLink(entity1A, entity1B);
		if (existingLink1 == null) {
			existingLink1 = new Link(entity1A, entity1B, new LinkType(LinkDecor.NONE, LinkDecor.NONE), Display.NULL, 2);
		} else {
			removeLink(existingLink1);
		}

		final IEntity entity1real = existingLink1.isInverted() ? existingLink1.getEntity2() : existingLink1
				.getEntity1();
		final IEntity entity2real = existingLink1.isInverted() ? existingLink1.getEntity1() : existingLink1
				.getEntity2();

		final Link entity1ToPoint = new Link(entity1real, point1, existingLink1.getType().getPart2(),
				existingLink1.getLabel(), existingLink1.getLength(), existingLink1.getQualifier1(), null,
				existingLink1.getLabeldistance(), existingLink1.getLabelangle());
		entity1ToPoint.setLinkArrow(existingLink1.getLinkArrow());
		final Link pointToEntity2 = new Link(point1, entity2real, existingLink1.getType().getPart1(), Display.NULL,
				existingLink1.getLength(), null, existingLink1.getQualifier2(), existingLink1.getLabeldistance(),
				existingLink1.getLabelangle());

		// int length = 1;
		// if (existingLink.getLength() == 1 && entity1A != entity1B) {
		// length = 2;
		// }
		// if (existingLink.getLength() == 2 && entity1A == entity1B) {
		// length = 2;
		// }

		addLink(entity1ToPoint);
		addLink(pointToEntity2);
	}

	public boolean associationClass(int mode, Code clName1, Code clName2, IEntity associed, LinkType linkType,
			Display label) {
		final IEntity entity1 = getOrCreateLeaf(clName1, null, null);
		final IEntity entity2 = getOrCreateLeaf(clName2, null, null);
		final List<Association> same = getExistingAssociatedPoints(entity1, entity2);
		if (same.size() > 1) {
			return false;
		} else if (same.size() == 0) {
			final Association association = new Association(mode, entity1, entity2, associed);
			association.createNew(mode, linkType, label);

			this.associations.add(association);
			return true;
		}
		assert same.size() == 1;
		final Association association = same.get(0).createSecondAssociation(mode, associed, label);
		association.createInSecond(linkType, label);

		this.associations.add(association);
		return true;
	}

	private List<Association> getExistingAssociatedPoints(final IEntity entity1, final IEntity entity2) {
		final List<Association> same = new ArrayList<Association>();
		for (Association existing : associations) {
			if (existing.sameCouple(entity1, entity2)) {
				same.add(existing);
			}
		}
		return same;
	}

	class Association {
		private IEntity entity1;
		private IEntity entity2;
		private IEntity associed;
		private IEntity point;

		private Link existingLink;

		private Link entity1ToPoint;
		private Link pointToEntity2;
		private Link pointToAssocied;

		private Association other;

		public Association(int mode, IEntity entity1, IEntity entity2, IEntity associed) {
			this.entity1 = entity1;
			this.entity2 = entity2;
			this.associed = associed;
			point = getOrCreateLeaf(UniqueSequence.getCode("apoint"), LeafType.POINT_FOR_ASSOCIATION, null);

		}

		public Association createSecondAssociation(int mode2, IEntity associed2, Display label) {
			final Association result = new Association(mode2, entity1, entity2, associed2);
			result.existingLink = this.existingLink;
			result.other = this;

			if (this.existingLink.getLength() == 1) {
				this.entity1ToPoint.setLength(2);
				this.pointToEntity2.setLength(2);
				this.pointToAssocied.setLength(1);
			}
			return result;
		}

		void createNew(int mode, LinkType linkType, Display label) {
			existingLink = foundLink(entity1, entity2);
			if (existingLink == null) {
				existingLink = new Link(entity1, entity2, new LinkType(LinkDecor.NONE, LinkDecor.NONE), Display.NULL, 2);
			} else {
				removeLink(existingLink);
			}

			final IEntity entity1real = existingLink.isInverted() ? existingLink.getEntity2() : existingLink
					.getEntity1();
			final IEntity entity2real = existingLink.isInverted() ? existingLink.getEntity1() : existingLink
					.getEntity2();

			entity1ToPoint = new Link(entity1real, point, existingLink.getType().getPart2(), existingLink.getLabel(),
					existingLink.getLength(), existingLink.getQualifier1(), null, existingLink.getLabeldistance(),
					existingLink.getLabelangle());
			entity1ToPoint.setLinkArrow(existingLink.getLinkArrow());
			pointToEntity2 = new Link(point, entity2real, existingLink.getType().getPart1(), Display.NULL,
					existingLink.getLength(), null, existingLink.getQualifier2(), existingLink.getLabeldistance(),
					existingLink.getLabelangle());

			int length = 1;
			if (existingLink.getLength() == 1 && entity1 != entity2) {
				length = 2;
			}
			if (existingLink.getLength() == 2 && entity1 == entity2) {
				length = 2;
			}
			if (length == 1) {
				entity1ToPoint.addNoteFrom(existingLink, NoteLinkStrategy.NORMAL);
			} else {
				entity1ToPoint.addNoteFrom(existingLink, NoteLinkStrategy.HALF_PRINTED_FULL);
				pointToEntity2.addNoteFrom(existingLink, NoteLinkStrategy.HALF_NOT_PRINTED);
			}
			addLink(entity1ToPoint);
			addLink(pointToEntity2);

			if (mode == 1) {
				pointToAssocied = new Link(point, associed, linkType, label, length);
			} else {
				pointToAssocied = new Link(associed, point, linkType, label, length);
			}
			addLink(pointToAssocied);
		}

		void createInSecond(LinkType linkType, Display label) {
			existingLink = foundLink(entity1, entity2);
			if (existingLink == null) {
				existingLink = new Link(entity1, entity2, new LinkType(LinkDecor.NONE, LinkDecor.NONE), Display.NULL, 2);
			} else {
				removeLink(existingLink);
			}

			entity1ToPoint = new Link(entity1, point, existingLink.getType().getPart2(), existingLink.getLabel(), 2,
					existingLink.getQualifier1(), null, existingLink.getLabeldistance(), existingLink.getLabelangle());
			pointToEntity2 = new Link(point, entity2, existingLink.getType().getPart1(), Display.NULL, 2, null,
					existingLink.getQualifier2(), existingLink.getLabeldistance(), existingLink.getLabelangle());
			// entity1ToPoint = new Link(entity1, point, existingLink.getType(),
			// null, 2);
			// pointToEntity2 = new Link(point, entity2, existingLink.getType(),
			// null, 2);
			addLink(entity1ToPoint);
			addLink(pointToEntity2);
			if (other.pointToAssocied.getEntity1().getLeafType() == LeafType.POINT_FOR_ASSOCIATION) {
				removeLink(other.pointToAssocied);
				other.pointToAssocied = other.pointToAssocied.getInv();
				addLink(other.pointToAssocied);
			}
			pointToAssocied = new Link(point, associed, linkType, label, 1);
			addLink(pointToAssocied);

			final Link lnode = new Link(other.point, this.point, new LinkType(LinkDecor.NONE, LinkDecor.NONE),
					Display.NULL, 1);
			lnode.setInvis(true);
			addLink(lnode);

		}

		boolean sameCouple(IEntity entity1, IEntity entity2) {
			if (this.entity1 == entity1 && this.entity2 == entity2) {
				return true;
			}
			if (this.entity1 == entity2 && this.entity2 == entity1) {
				return true;
			}
			return false;
		}
	}

}
