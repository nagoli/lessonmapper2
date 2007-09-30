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

import java.util.List;

import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.Type;
import org.exist.xquery.value.ValueSequence;

public class LMQuerySearch extends BasicFunction
{
   
    public LMQuerySearch(XQueryContext context, FunctionSignature signature)
    {
        super(context, signature);
    }
    
    /**
     * Function Signature
     */
    public final static FunctionSignature signature = new FunctionSignature(new QName("search",
            LMQueryModule.NAMESPACE_URI, LMQueryModule.PREFIX), "LM Search. $a query id.",
            new SequenceType[] { new SequenceType(Type.STRING, Cardinality.EXACTLY_ONE)}, 
            new SequenceType(Type.NODE, Cardinality.ZERO_OR_MORE));

    /**
     * Constructor
     */
    public LMQuerySearch(XQueryContext context)
    {
        super(context, signature);
    }

    /**
     * Actual evaluation of the function
     */
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException
    {
        //queryID must be specified
        if (args[0].isEmpty()) return Sequence.EMPTY_SEQUENCE;

        String queryID = args[0].getStringValue();
       //do the lm search  and returns an ordered list of document
        List<String> docNames = new LMQuery(queryID).getResults();

        
        Sequence theResults = new ValueSequence();
        for (String theString : docNames) 
			theResults.add(new StringValue(theString));
        return  theResults;
    }

}
