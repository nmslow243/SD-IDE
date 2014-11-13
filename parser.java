public class parser {

   static String inString = "BEGIN COMPUTE A1 = 34 * ABS ( 5 ) COMPUTE A2 = A1 + 12 WRITE A2 END EOF";
   static String remainingString = inString;
   static char BEGIN_CODE = 'B';
   static char END_CODE = 'E';
   static char COMPUTE_CODE = 'C';
   static char SQUARE_CODE = 'S';
   static char SQRT_CODE = 'R';
   static char ABS_CODE = 'A';
   static char PLUS_CODE = '+';
   static char MINUS_CODE = '-';
   static char AST_CODE = '*';
   static char SLASH_CODE = '/';
   static char ID_CODE = 'I';
   static char LEFT_PAREN_CODE = '(';
   static char RIGHT_PAREN_CODE = ')';
   static char EQUAL_CODE = '=';    
   static char END_OF_FILE = 'Z';
   static char INT_CODE = 'N';
   static char WRITE_CODE = 'W';
   static char nextToken;
   static String Token;
   static boolean error;

   public static void main (String args []) {

      lex();
      parse(); 

   }


   public static void lex() {

      System.out.print("Enter <lex> - lexeme = ");  
      
      String lexeme = "";
      int start = 0;

      while (start < remainingString.length() && remainingString.charAt(start)==' ')
         start++;

      int end = start+1;
      
      while (end < remainingString.length() && remainingString.charAt(end)!=' ')
         end++;

      if (start >= remainingString.length()) {
         lexeme = "";
         remainingString = "";
         System.out.print("EOF");
      }  
      else {
         lexeme = remainingString.substring(start,end);
         remainingString = remainingString.substring(end, remainingString.length());
      }
              
      if (lexeme.compareTo("BEGIN")==0) nextToken = BEGIN_CODE;
      else if (lexeme.compareTo("END")==0) nextToken = END_CODE;
      else if (lexeme.compareTo("COMPUTE")==0) nextToken = COMPUTE_CODE;
      else if (lexeme.compareTo("SQUARE")==0) nextToken = SQUARE_CODE;
      else if (lexeme.compareTo("SQRT")==0) nextToken = SQRT_CODE;
      else if (lexeme.compareTo("ABS")==0) nextToken = ABS_CODE;
      else if (lexeme.compareTo("+")==0) nextToken = PLUS_CODE;
      else if (lexeme.compareTo("-")==0) nextToken = MINUS_CODE;
      else if (lexeme.compareTo("*")==0) nextToken = AST_CODE;
      else if (lexeme.compareTo("/")==0) nextToken = SLASH_CODE;
      else if (lexeme.compareTo("(")==0) nextToken = LEFT_PAREN_CODE;
      else if (lexeme.compareTo(")")==0) nextToken = RIGHT_PAREN_CODE;
      else if (lexeme.compareTo("=")==0) nextToken = EQUAL_CODE;
      else if (lexeme.compareTo("WRITE")==0) nextToken = WRITE_CODE;
      else if (lexeme.compareTo("EOF")==0) nextToken = END_OF_FILE;
      else if (isNumeric(lexeme)) nextToken = INT_CODE;
      else nextToken = ID_CODE;
      Token = lexeme;
      System.out.print(lexeme+"  token = ");
      System.out.println(nextToken);

   }

   public static void parse() {
      String program;
      System.out.println("Enter <parse>");  
      program = program();
      System.out.println("Exit <parse>");
      if (error==false)  
         codeGen(program);
   } 

   public static String program() {
      String s;
      System.out.println("Enter <program>");
      s = "public class Evaluate\n{\n\tstatic String[][] varArray = new String[100][2];\n\tstatic int nextEmpty = 0;\n\n\tpublic static void main(String args[]) {";
      if (nextToken == BEGIN_CODE)
      {
    	 lex();
         s+=body()+"\n\t}";
      }
      else error();
      if (nextToken == END_CODE)
         lex();
      else error();
      System.out.println("Exit <program>");  
      s += "\n\tpublic static void setVar(String varName, int val) { \n\t\tfor(int i = 0; i < varArray.length; i++) {\n\t\t\tif (varArray[i][0] == varName) { \n\t\t\t\tvarArray[i][1] = val+\"\"; \n\t\t\t\treturn;\n\t\t\t}\n\t\t} \n\t\tvarArray[nextEmpty][0] = varName;\n\t\tvarArray[nextEmpty][1] = val+\"\";\n\t\tnextEmpty++;\n\t}\n";
      s += "\n\tpublic static int getVar(String varName) { \n\t\tfor(int i = 0; i < varArray.length; i++) {\n\t\t\tif (varArray[i][0] == varName) { \n\t\t\t\treturn Integer.parseInt(varArray[i][1]);\n\t\t\t} \n\t\t}\n\t\treturn 0;\n\t}";
		s += "\n\tpublic static int Square(int num) { \n\t\treturn num * num;\n\t}\n}";
      return s;
   } 

   public static String body() {
      String s="";
      System.out.println("Enter <body>");  
      do s += "\n\t\t"+stmt()+";"; while (nextToken != END_CODE && nextToken != END_OF_FILE && error == false);
      System.out.println("Exit <body>");  
      return s;

   } 
   public static String stmt() {
      String stmt="";
      String varName;
      System.out.println("Enter <stmt>");  
      
      if (nextToken == COMPUTE_CODE)
      {
         lex();
         
         if(nextToken == ID_CODE) {
            varName = Token;
            lex();
            if(nextToken == EQUAL_CODE){
               lex();
               stmt="setVar(\""+varName+"\","+expr()+")";
            } else {
               error();
            }
         } else { 
            error(); 
         }
      } else if (nextToken == WRITE_CODE) {
    	  lex();
    	  if (nextToken == ID_CODE) {
    		  stmt="System.out.println(getVar(\""+Token+"\"))";
    		  lex();
    	  } else error();
      } else error();
      System.out.println("Exit <stmt>");  
      return stmt;
   } 


   public static String expr() {
      String expr="";
      System.out.println("Enter <expr>"); 
      expr+=term();

      while (nextToken == PLUS_CODE || nextToken == MINUS_CODE) {
         expr+=" " + Token + " ";        
         lex();
         expr+=term();
      }
      System.out.println("Exit <expr>");  
      return expr;

   }

   public static String term() {
      String term = "";
      System.out.println("Enter <term>");  
      term+=factor();

      while (nextToken == AST_CODE || nextToken == SLASH_CODE ) {
         term+=" " + Token + " ";        
         lex();
         term+=factor();
      }
      System.out.println("Exit <term>");  
      return term;
   }

   public static String factor() {
      String factor = "";
      System.out.println("Enter <factor>");  
      if (nextToken == ID_CODE) {
         factor = "getVar(\""+Token+"\")";
         lex();
      } else if (nextToken == LEFT_PAREN_CODE) {
         lex();
         factor=Token+expr();
         if (nextToken == RIGHT_PAREN_CODE) {
            factor+=Token;          
            lex();
         } else
            error();
      } else if (nextToken == SQUARE_CODE || nextToken == SQRT_CODE || nextToken == ABS_CODE) {
         factor = function();
      } else if (nextToken == INT_CODE) {
         factor = Token;
         lex();
      } else error();
      System.out.println("Exit <factor>");  
      return factor;
   }

   public static String function() {
      String function="";
      boolean square = false;
      System.out.println("Enter <function>"); 
      if (nextToken == SQUARE_CODE) {
         function = " Square(";
         square = true;
      } 
      else function = " Math."+Token.toLowerCase()+"(";
      lex();  
      if (nextToken == LEFT_PAREN_CODE) {        
         lex();
         function+=expr();
         
         if (nextToken == RIGHT_PAREN_CODE) {
            function+=Token;  
            lex();
         } else
            error();
      }
      else error();
      System.out.println("Exit <function>"); 
      return function;
   }


   public static void error() {
    
      System.out.println("Enter <error>"); 
      error = true;
      System.out.println("Exit <error>"); 
    
   }
   public static boolean isNumeric(String str)  {  
      try  {  
         double d = Double.parseDouble(str);  
      } catch(NumberFormatException nfe)  {  
         return false;  
      }  
      return true;  
   }  
   public static void codeGen(String program) {
      java.io.FileOutputStream out;
      java.io.DataOutputStream dataOut;
      try {
      out = new java.io.FileOutputStream("Evaluate.java");
      dataOut = new java.io.DataOutputStream(out);
      dataOut.write(program.getBytes());
      dataOut.close();
      } catch (java.io.IOException e)   {
         System.out.println("Cannot Open or Use Output File");

      }
   }
}

