/*
 * LessonMapper 2.
Copyright (C) Olivier Motelet.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.exist.query;

import lessonMapper.exist.LMExistInitializer;

import org.exist.xquery.AbstractInternalModule;
import org.exist.xquery.FunctionDef;

/**
 * A module to permit access to the search function of lessonMapper
 * 
 */

public class LMQueryModule extends AbstractInternalModule {

	public final static String NAMESPACE_URI = "http://www.dcc.uchile.cl/lm2";
	
	public final static String PREFIX = "lm";
	
	private final static FunctionDef[] functions = {
        new FunctionDef(LMQuerySearch.signature, LMQuerySearch.class),
        new FunctionDef(LMQueryTrain.signature, LMQueryTrain.class),
        new FunctionDef(LMQueryIndex.signature, LMQueryIndex.class),
        new FunctionDef(LMConfig.signature, LMConfig.class),
        new FunctionDef(LMProba.signature, LMProba.class)
	};
	static {
		System.out.println("Initialize LMExist Config");
		 LMExistInitializer.initializeLocations();
	}

	public LMQueryModule() {
		super(functions);
	}

	public String getNamespaceURI() {
		return NAMESPACE_URI;
	}

	public String getDefaultPrefix() {
		return PREFIX;
	}

	public String getDescription() {
		return "LM search, index, training and configuration functions";
	}
}
