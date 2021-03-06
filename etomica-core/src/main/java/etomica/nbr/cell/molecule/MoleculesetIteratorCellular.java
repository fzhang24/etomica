/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package etomica.nbr.cell.molecule;

import etomica.lattice.CellLattice;

/**
 * Interface for moleculeset iterators that use cell-based neighbor
 * lists for iteration.  Defines a method that returns the iterator
 * that loops over the cells neighboring a given cell.  Access to
 * the cell iterator is needed to adjust the neighbor range defining
 * the neighbor cells.
 *
 * @author Tai Boon Tan
 *
 */
public interface MoleculesetIteratorCellular {

    /**
     * @return Returns the neighborCell Iterator.
     */
    public CellLattice.NeighborIterator getNbrCellIterator();
}