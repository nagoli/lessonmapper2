/* Generated By:JavaCC: Do not edit this line. RestrictionParser.java */
package lessonMapper.diffusion.restriction;




/**
 * 
 */
@SuppressWarnings("all")
public class RestrictionParser implements RestrictionParserConstants {

  /**
   * 
   */
  private static int count=0;

  /**
   * 
   * 
   * @param aBuilder 
   * 
   * @throws ParseException 
   */
  static final public void Heuristic(RestrictionHeuristicBuilder aBuilder) throws ParseException {
   String s;
   String o;
     count = 0;
    o = InitRestrictionOperator(aBuilder);
    jj_consume_token(45);
    s = FinalEquationAdd(aBuilder);
    jj_consume_token(0);
     aBuilder.setFinalStatement("return new lessonMapper.lom.LOMRestrictionValue("+o+","+s+");");
  }

  /**
   * 
   * 
   * @param aBuilder 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String RecurrentStatement(RestrictionHeuristicBuilder aBuilder) throws ParseException {
  String op;
  String eq;
  String cond=null;
    op = RecursiveOperator();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 46:
      jj_consume_token(46);
      cond = Condition();
      jj_consume_token(47);
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    jj_consume_token(48);
    eq = EquationAdd();
    jj_consume_token(49);
   String id = "recurs"+count++;
   aBuilder.addField("public lessonMapper.lom.LOMValue "+id+";");
   String theRecurrentStatement = "if ("+id+" == null) "+id+"= "+eq+"; \n"+
      "else "+id+" = "+id+"."+op+"("+eq+");";
   if (cond != null) theRecurrentStatement = "if (("+cond+")&&("+aBuilder.getDefaultFusionStrategy()+")){"
         +theRecurrentStatement+  "}";
   else theRecurrentStatement = "if ("+aBuilder.getDefaultFusionStrategy()+"){"
         +theRecurrentStatement+  "}";
   aBuilder.addRecurrentStatement(theRecurrentStatement);
   {if (true) return id;}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String Condition() throws ParseException {
String s;
String l=null;
String c=null;
    s = SimpleCondition();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OR:
    case AND:
      l = LogicOperator();
      c = Condition();
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
  if (l!=null) {if (true) return s+l+c;}
   {if (true) return s;}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String SimpleCondition() throws ParseException {
String e1;
String c;
String e2;
    if (jj_2_1(2147483647)) {
      e1 = EquationAdd();
      c = ConditionOperator();
      e2 = EquationAdd();
    {if (true) return e1+"."+c+"("+e2+")";}
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 48:
        jj_consume_token(48);
        c = Condition();
        jj_consume_token(49);
                          {if (true) return "("+c+")";}
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String ConditionOperator() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case eq:
      jj_consume_token(eq);
         {if (true) return "equals";}
      break;
    case inf:
      jj_consume_token(inf);
          {if (true) return "inferior";}
      break;
    case inf_eq:
      jj_consume_token(inf_eq);
             {if (true) return "inferiorOrEqual";}
      break;
    case sup:
      jj_consume_token(sup);
          {if (true) return "superior";}
      break;
    case sup_eq:
      jj_consume_token(sup_eq);
             {if (true) return  "superiorOrEqual";}
      break;
    case dif:
      jj_consume_token(dif);
          {if (true) return  "different";}
      break;
    case contains:
      jj_consume_token(contains);
               {if (true) return  "contains";}
      break;
    case contained:
      jj_consume_token(contained);
                {if (true) return  "contained";}
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String RestrictionOperator() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EQ:
      jj_consume_token(EQ);
        {if (true) return "EQ";}
      break;
    case INF:
      jj_consume_token(INF);
         {if (true) return "INF";}
      break;
    case INF_EQ:
      jj_consume_token(INF_EQ);
             {if (true) return "INF_EQ";}
      break;
    case SUP:
      jj_consume_token(SUP);
          {if (true) return "SUP";}
      break;
    case SUP_EQ:
      jj_consume_token(SUP_EQ);
             {if (true) return "SUP_EQ";}
      break;
    case DIF:
      jj_consume_token(DIF);
          {if (true) return "DIF";}
      break;
    case CONTAINS:
      jj_consume_token(CONTAINS);
                {if (true) return "CONTAINS";}
      break;
    case CONTAINED:
      jj_consume_token(CONTAINED);
                {if (true) return "CONTAINED";}
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @param aBuilder 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String InitRestrictionOperator(RestrictionHeuristicBuilder aBuilder) throws ParseException {
String o;
    o = RestrictionOperator();
 aBuilder.setRestrictionOperator(o);
 {if (true) return o;}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String String() throws ParseException {
 Token id;
    jj_consume_token(50);
    id = jj_consume_token(STRING);
    jj_consume_token(50);
    {if (true) return "new lessonMapper.lom.LOMValueVocabularySet(\"" + id.image + "\",ITSLOMAttribute)";}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String LogicOperator() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OR:
      jj_consume_token(OR);
            {if (true) return " || ";}
      break;
    case AND:
      jj_consume_token(AND);
             {if (true) return " && ";}
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String Attribute() throws ParseException {
 Token id;
    id = jj_consume_token(ATT_ID);
    {if (true) return "lessonMapper.lom.LOMAttribute.getLOMAttribute(\""+id.image+"\")";}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String RecursiveOperator() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case MAX:
      jj_consume_token(MAX);
          {if (true) return "max";}
      break;
    case MIN:
      jj_consume_token(MIN);
            {if (true) return "min";}
      break;
    case UNION:
      jj_consume_token(UNION);
              {if (true) return "union";}
      break;
    case INTER:
      jj_consume_token(INTER);
              {if (true) return "intersection";}
      break;
    case SUM:
      jj_consume_token(SUM);
             {if (true) return "sum";}
      break;
    case DIV:
      jj_consume_token(DIV);
            {if (true) return "division";}
      break;
    case PROD:
      jj_consume_token(PROD);
             {if (true) return "product";}
      break;
    case SUB:
      jj_consume_token(SUB);
            {if (true) return "subtraction";}
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String AdditiveOperator() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case union:
      jj_consume_token(union);
             {if (true) return "union";}
      break;
    case inter:
      jj_consume_token(inter);
              {if (true) return "intersection";}
      break;
    case sum:
      jj_consume_token(sum);
             {if (true) return "sum";}
      break;
    case sub:
      jj_consume_token(sub);
            {if (true) return "subtraction";}
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String MultiplicativeOperator() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case div:
      jj_consume_token(div);
            {if (true) return "division";}
      break;
    case prod:
      jj_consume_token(prod);
             {if (true) return "product";}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String EquationAdd() throws ParseException {
String theCall;
String op;
String e2;
    theCall = EquationMul();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case union:
      case inter:
      case sum:
      case sub:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_1;
      }
      op = AdditiveOperator();
      e2 = EquationMul();
    theCall+="."+op+"("+e2+")";
    }
     {if (true) return theCall;}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String EquationMul() throws ParseException {
String theCall;
String op;
String e2;
    theCall = EquationUni();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case div:
      case prod:
        ;
        break;
      default:
        jj_la1[10] = jj_gen;
        break label_2;
      }
      op = MultiplicativeOperator();
      e2 = EquationUni();
    theCall+="."+op+"("+e2+")";
    }
     {if (true) return theCall;}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String EquationUni() throws ParseException {
String e1;
String e2;
String n;
String att;
String s;
String o;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case max:
      jj_consume_token(max);
      jj_consume_token(48);
      e1 = EquationAdd();
      jj_consume_token(51);
      e2 = EquationAdd();
      jj_consume_token(49);
     {if (true) return e1+".max("+e2+")";}
      break;
    case min:
      jj_consume_token(min);
      jj_consume_token(48);
      e1 = EquationAdd();
      jj_consume_token(51);
      e2 = EquationAdd();
      jj_consume_token(49);
     {if (true) return e1+".min("+e2+")";}
      break;
    case sub:
    case NUM:
      n = Numeric();
                   {if (true) return n;}
      break;
    case 50:
      s = String();
                  {if (true) return s;}
      break;
    case OIJ:
      jj_consume_token(OIJ);
            {if (true) return "new lessonMapper.lom.LOMValueInt(oij,null)";}
      break;
    case EQ:
    case INF:
    case INF_EQ:
    case SUP:
    case SUP_EQ:
    case DIF:
    case CONTAINS:
    case CONTAINED:
      o = RestrictionOperator();
                              {if (true) return "new lessonMapper.lom.LOMValueInt("+o+",null)";}
      break;
    case VIJ:
      jj_consume_token(VIJ);
            {if (true) return "vij";}
      break;
    case LI:
      jj_consume_token(LI);
      jj_consume_token(52);
      att = Attribute();
        {if (true) return "lessonMapper.diffusion.Diffusion.ResVal(li,"+att+")";}
      break;
    case 48:
      jj_consume_token(48);
      e1 = EquationAdd();
      jj_consume_token(49);
           {if (true) return e1;}
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String Numeric() throws ParseException {
Token t;
boolean isPositive=true;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case sub:
      jj_consume_token(sub);
         isPositive=false;
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
    t = jj_consume_token(NUM);
  String theNum = "";
  if (!isPositive) theNum+="-";
  theNum += t.image;
  {if (true) return "new lessonMapper.lom.LOMValueInt("+theNum+",null)" ;}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @param aBuilder 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String FinalEquationAdd(RestrictionHeuristicBuilder aBuilder) throws ParseException {
String theCall;
String op;
String e2;
    theCall = FinalEquationMul(aBuilder);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case union:
      case inter:
      case sum:
      case sub:
        ;
        break;
      default:
        jj_la1[13] = jj_gen;
        break label_3;
      }
      op = AdditiveOperator();
      e2 = FinalEquationMul(aBuilder);
    theCall+="."+op+"("+e2+")";
    }
     {if (true) return theCall;}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @param aBuilder 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String FinalEquationMul(RestrictionHeuristicBuilder aBuilder) throws ParseException {
String theCall;
String op;
String e2;
    theCall = FinalEquationUni(aBuilder);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case div:
      case prod:
        ;
        break;
      default:
        jj_la1[14] = jj_gen;
        break label_4;
      }
      op = MultiplicativeOperator();
      e2 = FinalEquationUni(aBuilder);
    theCall+="."+op+"("+e2+")";
    }
     {if (true) return theCall;}
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @param aBuilder 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final public String FinalEquationUni(RestrictionHeuristicBuilder aBuilder) throws ParseException {
String id;
String n;
String e1;
String e2;
String s;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case max:
      jj_consume_token(max);
      jj_consume_token(48);
      e1 = FinalEquationAdd(aBuilder);
      jj_consume_token(51);
      e2 = FinalEquationAdd(aBuilder);
      jj_consume_token(49);
     {if (true) return e1+".max("+e2+")";}
      break;
    case min:
      jj_consume_token(min);
      jj_consume_token(48);
      e1 = FinalEquationAdd(aBuilder);
      jj_consume_token(51);
      e2 = FinalEquationAdd(aBuilder);
      jj_consume_token(49);
     {if (true) return e1+".min("+e2+")";}
      break;
    case MAX:
    case MIN:
    case UNION:
    case INTER:
    case SUM:
    case DIV:
    case PROD:
    case SUB:
      id = RecurrentStatement(aBuilder);
                                      {if (true) return id;}
      break;
    case 50:
      s = String();
                  {if (true) return s;}
      break;
    case sub:
    case NUM:
      n = Numeric();
                   {if (true) return n;}
      break;
    case 48:
      jj_consume_token(48);
      e1 = FinalEquationAdd(aBuilder);
      jj_consume_token(49);
     {if (true) return e1;}
      break;
    default:
      jj_la1[15] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  /**
   * 
   * 
   * @param xla 
   * 
   * @return 
   */
  static final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_12() {
    if (jj_scan_token(sup)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_33() {
    if (jj_scan_token(sub)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_32() {
    if (jj_scan_token(sum)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_31() {
    if (jj_scan_token(inter)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_11() {
    if (jj_scan_token(inf_eq)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_10() {
    if (jj_scan_token(inf)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_19() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_30()) {
    jj_scanpos = xsp;
    if (jj_3R_31()) {
    jj_scanpos = xsp;
    if (jj_3R_32()) {
    jj_scanpos = xsp;
    if (jj_3R_33()) return true;
    }
    }
    }
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_30() {
    if (jj_scan_token(union)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_6() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_9()) {
    jj_scanpos = xsp;
    if (jj_3R_10()) {
    jj_scanpos = xsp;
    if (jj_3R_11()) {
    jj_scanpos = xsp;
    if (jj_3R_12()) {
    jj_scanpos = xsp;
    if (jj_3R_13()) {
    jj_scanpos = xsp;
    if (jj_3R_14()) {
    jj_scanpos = xsp;
    if (jj_3R_15()) {
    jj_scanpos = xsp;
    if (jj_3R_16()) return true;
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_9() {
    if (jj_scan_token(eq)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3_1() {
    if (jj_3R_5()) return true;
    if (jj_3R_6()) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_40() {
    if (jj_scan_token(sub)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_34() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_40()) jj_scanpos = xsp;
    if (jj_scan_token(NUM)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_28() {
    if (jj_scan_token(48)) return true;
    if (jj_3R_5()) return true;
    if (jj_scan_token(49)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_27() {
    if (jj_scan_token(LI)) return true;
    if (jj_scan_token(52)) return true;
    if (jj_3R_37()) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_26() {
    if (jj_scan_token(VIJ)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_25() {
    if (jj_3R_36()) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_37() {
    if (jj_scan_token(ATT_ID)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_24() {
    if (jj_scan_token(OIJ)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_23() {
    if (jj_3R_35()) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_22() {
    if (jj_3R_34()) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_21() {
    if (jj_scan_token(min)) return true;
    if (jj_scan_token(48)) return true;
    if (jj_3R_5()) return true;
    if (jj_scan_token(51)) return true;
    if (jj_3R_5()) return true;
    if (jj_scan_token(49)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_20() {
    if (jj_scan_token(max)) return true;
    if (jj_scan_token(48)) return true;
    if (jj_3R_5()) return true;
    if (jj_scan_token(51)) return true;
    if (jj_3R_5()) return true;
    if (jj_scan_token(49)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_17() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_20()) {
    jj_scanpos = xsp;
    if (jj_3R_21()) {
    jj_scanpos = xsp;
    if (jj_3R_22()) {
    jj_scanpos = xsp;
    if (jj_3R_23()) {
    jj_scanpos = xsp;
    if (jj_3R_24()) {
    jj_scanpos = xsp;
    if (jj_3R_25()) {
    jj_scanpos = xsp;
    if (jj_3R_26()) {
    jj_scanpos = xsp;
    if (jj_3R_27()) {
    jj_scanpos = xsp;
    if (jj_3R_28()) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_35() {
    if (jj_scan_token(50)) return true;
    if (jj_scan_token(STRING)) return true;
    if (jj_scan_token(50)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_18() {
    if (jj_3R_29()) return true;
    if (jj_3R_17()) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_7() {
    if (jj_3R_17()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_18()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_48() {
    if (jj_scan_token(CONTAINED)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_47() {
    if (jj_scan_token(CONTAINS)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_46() {
    if (jj_scan_token(DIF)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_45() {
    if (jj_scan_token(SUP_EQ)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_44() {
    if (jj_scan_token(SUP)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_8() {
    if (jj_3R_19()) return true;
    if (jj_3R_7()) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_43() {
    if (jj_scan_token(INF_EQ)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_5() {
    if (jj_3R_7()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_8()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_42() {
    if (jj_scan_token(INF)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_41() {
    if (jj_scan_token(EQ)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_36() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_41()) {
    jj_scanpos = xsp;
    if (jj_3R_42()) {
    jj_scanpos = xsp;
    if (jj_3R_43()) {
    jj_scanpos = xsp;
    if (jj_3R_44()) {
    jj_scanpos = xsp;
    if (jj_3R_45()) {
    jj_scanpos = xsp;
    if (jj_3R_46()) {
    jj_scanpos = xsp;
    if (jj_3R_47()) {
    jj_scanpos = xsp;
    if (jj_3R_48()) return true;
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_39() {
    if (jj_scan_token(prod)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_16() {
    if (jj_scan_token(contained)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_29() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_38()) {
    jj_scanpos = xsp;
    if (jj_3R_39()) return true;
    }
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_38() {
    if (jj_scan_token(div)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_15() {
    if (jj_scan_token(contains)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_14() {
    if (jj_scan_token(dif)) return true;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private boolean jj_3R_13() {
    if (jj_scan_token(sup_eq)) return true;
    return false;
  }

  /**
   * 
   */
  static private boolean jj_initialized_once = false;
  
  /**
   * 
   */
  static public RestrictionParserTokenManager token_source;
  
  /**
   * 
   */
  static SimpleCharStream jj_input_stream;
  
  /**
   * 
   */
  static public Token token, jj_nt;
  
  /**
   * 
   */
  static private int jj_ntk;
  
  /**
   * 
   */
  static private Token jj_scanpos, jj_lastpos;
  
  /**
   * 
   */
  static private int jj_la;
  
  /**
   * 
   */
  static public boolean lookingAhead = false;
  
  /**
   * 
   */
  static private boolean jj_semLA;
  
  /**
   * 
   */
  static private int jj_gen;
  
  /**
   * 
   */
  static final private int[] jj_la1 = new int[16];
  
  /**
   * 
   */
  static private int[] jj_la1_0;
  
  /**
   * 
   */
  static private int[] jj_la1_1;
  static {
      jj_la1_0();
      jj_la1_1();
   }
   
   /**
    * 
    */
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x0,0x0,0x0,0xff000000,0x0,0x0,0x1fe000,0x1380,0xc00,0x1380,0xc00,0xe01060,0x1000,0x1380,0xc00,0x1ff060,};
   }
   
   /**
    * 
    */
   private static void jj_la1_1() {
      jj_la1_1 = new int[] {0x4000,0x3,0x10000,0x0,0x1fe0,0x3,0x0,0x0,0x0,0x0,0x0,0x51ff0,0x0,0x0,0x0,0x50010,};
   }
  
  /**
   * 
   */
  static final private JJCalls[] jj_2_rtns = new JJCalls[1];
  
  /**
   * 
   */
  static private boolean jj_rescan = false;
  
  /**
   * 
   */
  static private int jj_gc = 0;

  /**
   * 
   * 
   * @param stream 
   */
  public RestrictionParser(java.io.InputStream stream) {
     this(stream, null);
  }
  
  /**
   * 
   * 
   * @param encoding 
   * @param stream 
   */
  public RestrictionParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new RestrictionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /**
   * 
   * 
   * @param stream 
   */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  
  /**
   * 
   * 
   * @param encoding 
   * @param stream 
   */
  
static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /**
   * 
   * 
   * @param stream 
   */
  public RestrictionParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new RestrictionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /**
   * 
   * 
   * @param stream 
   */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /**
   * 
   * 
   * @param tm 
   */
  public RestrictionParser(RestrictionParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /**
   * 
   * 
   * @param tm 
   */
  public void ReInit(RestrictionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /**
   * 
   * 
   * @param kind 
   * 
   * @return 
   * 
   * @throws ParseException 
   */
  static final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  /**
   * 
   */
  static private final class LookaheadSuccess extends java.lang.Error { }
  
  /**
   * 
   */
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  
  /**
   * 
   * 
   * @param kind 
   * 
   * @return 
   */
  static final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  /**
   * 
   * 
   * @return 
   */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  /**
   * 
   * 
   * @param index 
   * 
   * @return 
   */
  static final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  /**
   * 
   * 
   * @return 
   */
  static final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  /**
   * 
   */
  static private java.util.Vector jj_expentries = new java.util.Vector();
  
  /**
   * 
   */
  static private int[] jj_expentry;
  
  /**
   * 
   */
  static private int jj_kind = -1;
  
  /**
   * 
   */
  static private int[] jj_lasttokens = new int[100];
  
  /**
   * 
   */
  static private int jj_endpos;

  /**
   * 
   * 
   * @param kind 
   * @param pos 
   */
  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /**
   * 
   * 
   * @return 
   */
  static public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[53];
    for (int i = 0; i < 53; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 16; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 53; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /**
   * 
   */
  static final public void enable_tracing() {
  }

  /**
   * 
   */
  static final public void disable_tracing() {
  }

  /**
   * 
   */
  static final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  /**
   * 
   * 
   * @param index 
   * @param xla 
   */
  static final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  /**
   * 
   */
  static final class JJCalls {
    
    /**
     * 
     */
    int gen;
    
    /**
     * 
     */
    Token first;
    
    /**
     * 
     */
    int arg;
    
    /**
     * 
     */
    JJCalls next;
  }

}
