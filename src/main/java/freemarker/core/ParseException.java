/*
 * Copyright (c) 2003 The Visigoth Software Society. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Visigoth Software Society (http://www.visigoths.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. Neither the name "FreeMarker", "Visigoth", nor any of the names of the
 *    project contributors may be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact visigoths@visigoths.org.
 *
 * 5. Products derived from this software may not be called "FreeMarker" or "Visigoth"
 *    nor may "FreeMarker" or "Visigoth" appear in their names
 *    without prior written permission of the Visigoth Software Society.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE VISIGOTH SOFTWARE SOCIETY OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Visigoth Software Society. For more
 * information on the Visigoth Software Society, please see
 * http://www.visigoths.org/
 */

package freemarker.core;

import freemarker.template.Template;
import freemarker.template.utility.SecurityUtilities;

/**
 * Exception thrown on parsing errors that are not lexical errors.
 *
 * This is a modified version of file generated by JavaCC from FTL.jj.
 * You can modify this class to customize the error reporting mechanisms so long as the public interface
 * remains compatible with the original.
 * 
 * @see TokenMgrError
 */
public class ParseException extends java.io.IOException implements FMParserConstants {

   // This was no part of Throwable on J2SE 1.2
   private final Throwable cause;
   
   /**
    * This is the last token that has been consumed successfully.  If
    * this object has been created due to a parse error, the token
    * following this token will (therefore) be the first error token.
    */
   public Token currentToken;
   public String cachedUnexpectedTokenMessage;

   private String templateName;
   
   public int columnNumber, lineNumber;

   /**
    * Each entry in this array is an array of integers.  Each array
    * of integers represents a sequence of tokens (by their ordinal
    * values) that is expected at this point of the parse.
    */
   public int[][] expectedTokenSequences;

   /**
    * This is a reference to the "tokenImage" array of the generated
    * parser within which the parse error occurred.  This array is
    * defined in the generated ...Constants interface.
    */
   public String[] tokenImage;
   

  /**
   * This constructor is used by the method "generateParseException"
   * in the generated parser.  Calling this constructor generates
   * a new object of this type with the fields "currentToken",
   * "expectedTokenSequences", and "tokenImage" set.
   * This constructor calls its super class with the empty string
   * to force the "toString" method of parent class "Throwable" to
   * print the error message in the form:
   *     ParseException: <result of getMessage>
   */
  public ParseException(Token currentTokenVal,
                        int[][] expectedTokenSequencesVal,
                        String[] tokenImageVal
                       )
  {
    super("");
    cause = null;
    currentToken = currentTokenVal;
    expectedTokenSequences = expectedTokenSequencesVal;
    tokenImage = tokenImageVal;
    lineNumber = currentToken.next.beginLine;
    columnNumber = currentToken.next.beginColumn;
  }

  /**
   * The following constructors are for use by you for whatever
   * purpose you can think of.  Constructing the exception in this
   * manner makes the exception behave in the normal way - i.e., as
   * documented in the class "Throwable".  The fields "errorToken",
   * "expectedTokenSequences", and "tokenImage" do not contain
   * relevant information.  The JavaCC generated code does not use
   * these constructors.
   */
  protected ParseException() {
    super();
    cause = null;
  }

  /**
   * @deprecated Use a constructor to which you can also pass the template.
   */
  public ParseException(String details, int lineNumber, int columnNumber) {
      this(details, (Template) null, lineNumber, columnNumber, null);
  }

  /**
   * @since 2.3.20
   */
  public ParseException(String details, Template template, int lineNumber, int columnNumber, Throwable cause) {
      this(details,
              template == null ? null : template.getName(),
              lineNumber, columnNumber,
              cause);      
  }

  /**
   * @since 2.3.20
   */
  public ParseException(String details, Template template, Token tk) {
      this(details, template, tk, null);
  }
  
  /**
   * @since 2.3.20
   */
  public ParseException(String details, Template template, Token tk, Throwable cause) {
      this(details,
              template == null ? null : template.getName(),
              tk.beginLine, tk.beginColumn,
              cause);
  }

  /**
   * @since 2.3.20
   */
  public ParseException(String details, TemplateObject tobj) {
      this(details, tobj, null);
  }

  /**
   * @since 2.3.20
   */
  public ParseException(String details, TemplateObject tobj, Throwable cause) {
      this(details,
              tobj.getTemplate() == null ? null : tobj.getTemplate().getName(),
              tobj.beginLine, tobj.beginColumn,
              cause);
  }

  private ParseException(String details, String templateName, int lineNumber, int columnNumber, Throwable cause) {
      super(formatMessage(details, templateName, lineNumber, columnNumber));
      this.cause = cause;
      
      this.templateName = templateName;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
  }
  
  private static String formatMessage(String details, String templateName, int lineNumber, int columnNumber) {
      return "Error "
              + MessageUtil.formatLocationForSimpleParsingError(templateName, lineNumber, columnNumber)
              + ":\n" + details;      
  }

  /**
   * Sets the name of the template that contains the error.
   * This is needed as the constructor that JavaCC automatically calls doesn't pass in the template, so we
   * set it somewhere later in an exception handler. 
   */
  public void setTemplateName(String templateName) {
      this.templateName = templateName;
      cachedUnexpectedTokenMessage = null;
  }
  
  /**
   * This method has the standard behavior when this object has been
   * created using the standard constructors.  Otherwise, it uses
   * "currentToken" and "expectedTokenSequences" to generate a parse
   * error message and returns it.  If this object has been created
   * due to a parse error, and you do not catch it (it gets thrown
   * from the parser), then this method is called during the printing
   * of the final stack trace, and hence the correct error message
   * gets displayed.
   */
  public String getMessage() {
    if (currentToken == null) {
      return super.getMessage();
    }
    
    if (cachedUnexpectedTokenMessage == null) {
        String details = getCustomUnexpectedTokenDetails();
        if (details == null) {
            // The default JavaCC message generation stuff follows.
            String expected = "";
            int maxSize = 0;
            for (int i = 0; i < expectedTokenSequences.length; i++) {
              if (maxSize < expectedTokenSequences[i].length) {
                maxSize = expectedTokenSequences[i].length;
              }
              for (int j = 0; j < expectedTokenSequences[i].length; j++) {
                expected += tokenImage[expectedTokenSequences[i][j]] + " ";
              }
              if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
                expected += "...";
              }
              expected += eol + "    ";
            }
            details = "Encountered \"";
            Token tok = currentToken.next;
            for (int i = 0; i < maxSize; i++) {
              if (i != 0) details += " ";
              if (tok.kind == 0) {
                details += tokenImage[0];
                break;
              }
              details += add_escapes(tok.image);
              tok = tok.next;
            }
            details += "\"" + eol;
            
            if (expectedTokenSequences.length == 1) {
              details += "Was expecting:" + eol + "    ";
            } else {
              details += "Was expecting one of:" + eol + "    ";
            }
            details += expected;
        }
        cachedUnexpectedTokenMessage = formatMessage(details, templateName, lineNumber, columnNumber);
    }
    return cachedUnexpectedTokenMessage;
  }
  
  public String getTemplateName() {
      return templateName;
  }

  /**
   * 1-based line number of the failing token.
   */
  public int getLineNumber() {
      return lineNumber;
  }

  /**
   * 1-based column number of the failing token.
   */
  public int getColumnNumber() {
      return columnNumber;
  }

  // Custom message generation

  private String getCustomUnexpectedTokenDetails() {
      final Token nextToken = currentToken.next;
      final int kind = nextToken.kind;
      if (kind == EOF) {
          StringBuffer buf = new StringBuffer("Unexpected end of file reached.\n");
          for (int i = 0; i<expectedTokenSequences.length; i++) {
              int[] sequence = expectedTokenSequences[i];
              switch (sequence[0]) {
                  case END_FOREACH :
                      buf.append("Unclosed \"foreach\" directive.\n");
                      break;
                  case END_LIST :
                      buf.append("Unclosed \"list\" directive.\n");
                      break;
                  case END_SWITCH :
                      buf.append("Unclosed \"switch\" directive.\n");
                      break;
                  case END_IF :
                      buf.append("Unclosed \"if\" directive.\n");
                      break;
                  case END_COMPRESS :
                      buf.append("Unclosed \"compress\" directive.\n");
                      break;
                  case END_MACRO :
                      buf.append("Unclosed \"macro\" directive.\n");
                      break;
                  case END_FUNCTION :
                      buf.append("Unclosed \"function\" directive.\n");
                      break;
                  case END_TRANSFORM :
                      buf.append("Unclosed \"transform\" directive.\n");
                      break;
                  case END_ESCAPE :
                      buf.append("Unclosed \"escape\" directive.\n");
                      break;
                  case END_NOESCAPE :
                      buf.append("Unclosed \"noescape\" directive.\n");
                      break;
              }
          }
          return buf.toString();
      } else if (kind == END_IF || kind == ELSE_IF || kind == ELSE) {
          return "Found unexpected directive: "
              + nextToken
              + "\nCheck whether you have a well-formed if-else block.";
      }
      return null;
  }

  /**
   * The end of line string for this machine.
   */
  protected final String eol = SecurityUtilities.getSystemProperty("line.separator", "\n");

  /**
   * Used to convert raw characters to their escaped version
   * when these raw version cannot be used as part of an ASCII
   * string literal.
   */
  protected String add_escapes(String str) {
      StringBuffer retval = new StringBuffer();
      char ch;
      for (int i = 0; i < str.length(); i++) {
        switch (str.charAt(i))
        {
           case 0 :
              continue;
           case '\b':
              retval.append("\\b");
              continue;
           case '\t':
              retval.append("\\t");
              continue;
           case '\n':
              retval.append("\\n");
              continue;
           case '\f':
              retval.append("\\f");
              continue;
           case '\r':
              retval.append("\\r");
              continue;
           case '\"':
              retval.append("\\\"");
              continue;
           case '\'':
              retval.append("\\\'");
              continue;
           case '\\':
              retval.append("\\\\");
              continue;
           default:
              if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
                 String s = "0000" + Integer.toString(ch, 16);
                 retval.append("\\u" + s.substring(s.length() - 4, s.length()));
              } else {
                 retval.append(ch);
              }
              continue;
        }
      }
      return retval.toString();
   }
  
  public Throwable getCause() {
      return cause;
  }

}
