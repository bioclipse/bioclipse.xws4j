package net.bioclipse.xws4j.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import net.bioclipse.xws4j.views.servicediscovery.ServiceDiscoveryView;
/**
 * 
 * This file is part of the Bioclipse xws4j Plug-in.
 * 
 * Copyright (C) 2008 Johannes Wagener
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * @author Johannes Wagener
 */
public class ShowServiceDiscoveryViewAction extends Action {
        public static class ShowServiceDiscoveryViewContribution extends ActionContributionItem {
                public ShowServiceDiscoveryViewContribution() {
                        super (getStatic());
                }
        }
        private static ShowServiceDiscoveryViewAction static_action = null;
        public static ShowServiceDiscoveryViewAction getStatic() {
                if (static_action == null) {
                        static_action = new ShowServiceDiscoveryViewAction();
                }
                return static_action;
        }
        public ShowServiceDiscoveryViewAction() {
                super("Show XMPP Service Discovery View");
        }
        public void run() {
        	ServiceDiscoveryView.show();
        }
}