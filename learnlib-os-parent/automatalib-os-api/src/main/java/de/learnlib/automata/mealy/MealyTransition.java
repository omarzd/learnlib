/* Copyright (C) 2012 TU Dortmund
   This file is part of LearnLib 

   LearnLib is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License version 3.0 as published by the Free Software Foundation.

   LearnLib is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with LearnLib; if not, see
   <http://www.gnu.de/documents/lgpl.en.html>.  */

package de.learnlib.automata.mealy;

import de.learnlib.automata.Symbol;

/**
 *
 * @author merten
 */
public interface MealyTransition {
	
	public void setTargetState(MealyState state);
	
	public MealyState getTargetState();
	
	
	public void setOutputSymbol(Symbol outputsym);
	
	public Symbol getOutputSymbol();
	
	
	
}