package net.bioclipse.xws4j.actions;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import net.bioclipse.xws4j.XwsLogPipe;
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
public class DebugModeAction extends Action {
        private static DebugModeAction static_action = null;
        public static class DebugModeActionContribution extends ActionContributionItem {
                public DebugModeActionContribution() {
                        super (getStatic());
                }
        }
        public static DebugModeAction getStatic() {
                if (static_action == null) {
                        static_action = new DebugModeAction();
                        static_action.setChecked(XwsLogPipe.isDebugMode());
                }
                return static_action;
        }
        public DebugModeAction() {
                super("Debug Mode", Action.AS_CHECK_BOX);
        }
        public void run() {
                XwsLogPipe.setDebugMode(!XwsLogPipe.isDebugMode());
                setChecked(XwsLogPipe.isDebugMode());
        }
}