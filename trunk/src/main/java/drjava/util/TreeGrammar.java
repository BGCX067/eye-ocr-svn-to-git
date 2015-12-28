/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import gi.Grammar;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

/** This class is an ad-hoc interpreter constructed with the magnificient GI */
public class TreeGrammar extends Grammar {
  private static final String idChars1 = "_$?-#";
  private static final String idChars2 = "_$-.";

  public TreeGrammar() {
    /**
    * Nonterminal - first letter uppercase
    * TERMINAL - all letters uppercase
    * keyword - all letters lowercase
    */

    // first production must be main parsing target

    put("Goal", new Object[][] {
      {"CompilationUnit"}
    });

    // now add lexical rules

    lexicalRules();

    // ...and all the other productions

    productions();
  }

  private Tree tree;
  private Stack<Tree> termStacks;

  static class Arg {
    String name;
    Tree value;

    public Arg(String name, Tree value) {
      this.name = name;
      this.value = value;
    }

    public Arg(Tree value) {
      this.value = value;
    }
  }

  private void productions() {
    put("CompilationUnit", new Object[][] {
      {},
      { "Declarations", "Point_opt" },
    });

    put("Point_opt", new Object[][] {
      {},
      { "." },
    });

    put("Declarations", new Object[][] {
      { "Declarations", ".", "Declaration" },
      { "Declaration" }
    });

    put("Declaration", new Object[][] {
      { "Term", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          Tree item = termStacks.pop();
          tree = item;
        }
      }}
    });

    put("Term", new Object[][] {
      { "Term3" },
      { "Id", "IdTerm", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          Tree tree = termStacks.pop();
          termStacks.push(new Tree((String) t.child[0].value, tree));
        }
      }},
    });

    put("Term3", new Object[][] {
      { "IdTerm" },
      { "(", "Term", ")", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          //t.value = new Baum((String) t.child[1].value);
        }
      }},
    });

    Semantics literalSemantics = new Semantics() {
      protected void f(ParseTree t, int l) throws Exception {
        Tree tree = new Tree((String) t.child[0].value);
        termStacks.push(tree);
        //t.value = baum;
      }
    };
    put("IdTerm", new Object[][] {
      { "Id", "KlammerAuf", "KlammerZu", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          termStacks.push(new Tree((String) t.child[0].value));
        }
      }},
      { "Id", "KlammerAuf", "Attrs", "KlammerZu", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          Tree tree = termStacks.pop();
          tree.setName((String) t.child[0].value);
          termStacks.push(tree);
        }
      }},
      { "Id", literalSemantics },
      /*{ "?", "IDENTIFIER", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          Baum baum = new Baum((String) t.child[1].value);
          termStack.push(baum);
          super.f(t, l);
        }
      }},*/
    });

    put("KlammerAuf", new Object[][] {
      { "(" },
      { "{" },
    });

    put("KlammerZu", new Object[][] {
      { ")" },
      { "}" },
    });

    put("Attrs", new Object[][] {
      { "Attrs", ",", "Attr", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          Tree tree = termStacks.peek();
          String argName = ((Arg) t.child[2].value).name;
          Tree arg = ((Arg) t.child[2].value).value;
          if (argName != null) tree.add(argName, arg); else tree.add(arg);
        }
      }},
      { "Attr", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          Tree tree = new Tree("_"); // name will be replaced
          String argName = ((Arg) t.child[0].value).name;
          Tree arg = ((Arg) t.child[0].value).value;
          if (argName != null) tree.add(argName, arg); else tree.add(arg);
          termStacks.push(tree);
        }
      }},
    });

    put("Attr", new Object[][] {
      { "Term", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          t.value = new Arg(termStacks.pop());
        }
      }},
      { "Id", "=", "Term", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          t.value = new Arg((String) t.child[0].value, termStacks.pop());
        }
      }},
    });

    put("Id", new Object[][] {
      { "IDENTIFIER", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          t.value = t.child[0].value;
        }
      }},
      { "INTEGER_LITERAL", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          t.value = t.child[0].value;
        }
      }},
      { "FLOATING_POINT_LITERAL", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          t.value = t.child[0].value;
        }
      }},
      { "STRING_LITERAL", new Semantics() {
        protected void f(ParseTree t, int l) throws Exception {
          t.value = Tree.unquoteString((String) t.child[0].value);
        }
      }},
    });

    // add new productions here
  }

  void lexicalRules() {
    // These rules are taken from Java10.java (part of gi-0.9 distribution)

		int INFINITY = -1;

		/**
		* 19.3 Terminals from section 3.6: White Space: [[:space:]]
		*/
		put("WHITE_SPACE", PosixClass.space());

		/**
		* 19.3 Terminals from section 3.7: Comment
		*/
		put("COMMENT", new Union(

			//
			// Traditional Comment: /\*[^*]+(\*([^*/][^*]*)?)*\*/
			//
			new Concatenation(
				new Singleton("/*"), new Concatenation(
				new Repetition(new NonMatch("*"), 1, INFINITY), new Concatenation(
				new Repetition(
					new Concatenation(
						new Singleton("*"),
						new Repetition(new Concatenation(
							new NonMatch("*/"),
							new Repetition(new NonMatch("*"), 0, INFINITY)
						), 0, 1)
					), 0, INFINITY
				),
				new Singleton("*/")
			))), new Union(

			/**
			* End Of Line Comment: //[^\n]*\n
			*/
			new Concatenation(
				new Singleton("//"), new Concatenation(
				new Repetition(new NonMatch("\n"), 0, INFINITY),
				new Singleton("\n")
			)),

			//
			// Documentation Comment: /\*\*(([^*/][^*]*)?\*)*/
			//
			new Concatenation(
				new Singleton("/**"), new Concatenation(
				new Repetition(
					new Concatenation(
						new Repetition(new Concatenation(
							new NonMatch("*/"),
							new Repetition(new NonMatch("*"), 0, INFINITY)
						), 0, 1),
						new Singleton("*")
					), 0, INFINITY
				),
				new Singleton("/")
			))
		)));

		/**
		* see also isIdentifier()
		*/
		put("IDENTIFIER_opt", new Object[][] {
			{},
			{"IDENTIFIER"}
		});
		put("IDENTIFIER", new Concatenation(
			new Union(
				PosixClass.alpha(),
				new Match(idChars1)
			),
			new Repetition(
				new Union(
					PosixClass.alnum(),
					new Match(idChars2)
				), 0, INFINITY
			)
		));

		/**
		* 19.3 Terminals from section 3.10.1: Integer Literal
		*/
		put("INTEGER_LITERAL", new Concatenation(
			new Union(
				/**
				* Decimal Integer Literal: 0|[1-9][[:digit:]]*
				*/
				new Singleton("0"), new Union(

				new Concatenation(
					new Range('1', '9'),
					new Repetition(PosixClass.digit(), 0, INFINITY)
				), new Union(

				/**
				* Hexadecimal Integer Literal: 0[xX][[:xdigit:]]+
				*/
				new Concatenation(
					new Singleton("0"), new Concatenation(
					new Match("xX"),
					new Repetition(PosixClass.xdigit(), 1, INFINITY)
				)),

				/**
				* Octal Integer Literal: 0[0-7]+
				*/
				new Concatenation(
					new Singleton("0"),
					new Repetition(new Range('0', '7'), 1, INFINITY)
				)
			))),
			new Repetition(new Match("lL"), 0, 1)
		));

		/**
		* 19.3 Terminals from section 3.10.5: String Literal
		*/
		put("STRING_LITERAL", new Concatenation(
			new Singleton("\""), new Concatenation(
			new Repetition(
				new Union(

					/**
					* Single Character: [^\r\n"\\]
					*/
					new NonMatch("\r\n\"\\"),

					/**
					* Escape Sequence: \\([btnfr\"'\\]|[0-3]?[0-7]{1,2})
					*/
					new Concatenation(
						new Singleton("\\"),
						new Union(
							new Match("btnfr\"'\\"),
							new Concatenation(
								new Repetition(new Range('0', '3'), 0, 1),
								new Repetition(new Range('0', '7'), 1, 2)
							)
						)
					)
				), 0, INFINITY
			),
			new Singleton("\"")
		)));

    /**
    * 19.3 Terminals from section 3.10.2: Floating-Point Literal
    */
    put("FLOATING_POINT_LITERAL", new Union(

      /**
      * [[:digit:]]+\.[[:digit:]]*([eE][-+]?[[:digit:]]+)?[fFdD]?
      */
      new Concatenation(
        new Repetition(PosixClass.digit(), 1, INFINITY), new Concatenation(
        new Singleton("."), new Concatenation(
        new Repetition(PosixClass.digit(), 0, INFINITY), new Concatenation(
        new Repetition(new Concatenation(
          new Match("eE"), new Concatenation(
          new Repetition(new Match("-+"), 0, 1),
          new Repetition(PosixClass.digit(), 1, INFINITY)
        )), 0, 1),
        new Repetition(new Match("fFdD"), 0, 1)
      )))), new Union(

      /**
      * \.[[:digit:]]+([eE][-+]?[[:digit:]]+)?[fFdD]?
      */
      new Concatenation(
        new Singleton("."), new Concatenation(
        new Repetition(PosixClass.digit(), 1, INFINITY), new Concatenation(
        new Repetition(new Concatenation(
          new Match("eE"), new Concatenation(
          new Repetition(new Match("-+"), 0, 1),
          new Repetition(PosixClass.digit(), 1, INFINITY)
        )), 0, 1),
        new Repetition(new Match("fFdD"), 0, 1)
      ))), new Union(

      /**
      * [[:digit:]]+[eE][-+]?[[:digit:]]+[fFdD]?
      */
      new Concatenation(
        new Repetition(PosixClass.digit(), 1, INFINITY), new Concatenation(
        new Match("eE"), new Concatenation(
        new Repetition(new Match("-+"), 0, 1), new Concatenation(
        new Repetition(PosixClass.digit(), 1, INFINITY),
        new Repetition(new Match("fFdD"), 0, 1)
      )))),

      /**
      * [[:digit:]]+([eE][-+]?[[:digit:]]+)?[fFdD]
      */
      new Concatenation(
        new Repetition(PosixClass.digit(), 1, INFINITY), new Concatenation(
        new Repetition(new Concatenation(
          new Match("eE"), new Concatenation(
          new Repetition(new Match("-+"), 0, 1),
          new Repetition(PosixClass.digit(), 1, INFINITY)
        )), 0, 1),
        new Match("fFdD")
      ))
    ))));
  }

  public Tree parse(File file) throws IOException {
    prepare();
    try {
      interpret(file);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException(e.toString());
    }
    return tree;
  }

  private void prepare() {
    tree = null;
    termStacks = new Stack<Tree>();
  }

  public Tree parse(String text) throws IOException {
    prepare();
    try {
      interpret(text);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return tree;
  }

  protected void putWithOptAndPlural(String singular, Object[][] definitions) {
    // Without the .intern(), we would get into BIG trouble - GI uses == rather than
    // equals() to compare symbols
    String plural = (singular+"s").intern(), opt = (singular+"s_opt").intern();

		put(opt, new Object[][] { {/*optSemantics*/}, {plural, /*optSemantics*/} });
		put(plural, new Object[][] { {singular, /*pluralSemantics*/}, {plural, singular, /*pluralSemantics*/} });
    put(singular, definitions);
  }

  /** must match same set as or subset of IDENTIFIER */
  public static boolean isIdentifier(String s) {
    if (s.length() == 0) return false;
    if (!Character.isLetter(s.charAt(0)) && idChars1.indexOf(s.charAt(0)) < 0) return false;
    for (int i = 1; i < s.length(); i++)
      if (!Character.isLetterOrDigit(s.charAt(i)) && idChars2.indexOf(s.charAt(i)) < 0) return false;
    return true;
  }

  public static boolean isInteger(String s) {
    if (s.length() == 0) return false;
    for (int i = 0; i < s.length(); i++)
      if (!Character.isDigit(s.charAt(i))) return false;
    return true;
  }
}