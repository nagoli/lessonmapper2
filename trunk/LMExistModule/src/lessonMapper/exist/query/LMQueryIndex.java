
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

import lessonMapper.exist.lucene.LuceneIndexer;

import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.EmptySequence;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;

public class LMQueryIndex extends BasicFunction
{
   
    public LMQueryIndex(XQueryContext context, FunctionSignature signature)
    {
        super(context, signature);
    }
    
    /**
     * Function Signature
     */
    public final static FunctionSignature signature = new FunctionSignature(new QName("rebuildIndex",
            LMQueryModule.NAMESPACE_URI, LMQueryModule.PREFIX), "LM Rebuild Index",
            new SequenceType[] {}, 
            new SequenceType(Type.NODE, Cardinality.ZERO_OR_MORE));

    /**
     * Constructor
     */
    public LMQueryIndex(XQueryContext context)
    {
        super(context, signature);
    }

    /**
     * reindex the db
     */
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException
    {
    	LuceneIndexer.ITSINSTANCE.reindex();
        Sequence theResults = new EmptySequence();
        return  theResults;
    }

}
