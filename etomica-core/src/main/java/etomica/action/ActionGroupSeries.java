/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package etomica.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A set of Action instances grouped and performed in series
 * as if a single action.  Actions may be defined at construction
 * and/or added afterward.  Actions are performed in the order in
 * which they are specifed in the constructor and subsequently added.
 *
 * @author David Kofke
 */
public class ActionGroupSeries implements ActionGroup {

    private final List<IAction> actions;

    /**
     * Constructs an action group that holds no actions.
     */
    public ActionGroupSeries() {
        this.actions = new ArrayList<>();
    }

    /**
     * Defines group via the given array of actions.  Copy
     * of array is made and used internally.
     */
    public ActionGroupSeries(IAction[] actions) {
        this.actions = new ArrayList<>(Arrays.asList(actions));
    }

    /**
     * Invokes the actionPerformed method of all actions
     * in the method, in the order given by the array at construction.
     */
    public void actionPerformed() {
        for(IAction action : this.actions) {
            action.actionPerformed();
        }
    }

    /**
     * Removes the given action from the group.
     *
     * @return true if the action was present and removed, false if
     * the action was not present.
     */
    public boolean removeAction(IAction oldAction) {
        return this.actions.remove(oldAction);
    }

    /**
     * Adds the given action to the group.  No check is made of whether
     * action is already in group; it is added regardless.
     *
     * @param newAction
     */
    public void addAction(IAction newAction) {
        this.actions.add(newAction);
    }

    public IAction[] getAllActions() {
        return actions.toArray(new IAction[]{});
    }
}
