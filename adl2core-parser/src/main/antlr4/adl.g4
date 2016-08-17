/*
 * ADL2-core
 * Copyright (c) 2013-2014 Marand d.o.o. (www.marand.com)
 *
 * This file is part of ADL2-core.
 *
 * ADL2-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

grammar adl;


adl: header specialize? concept? language? description? definition? terminology? annotations? EOF;

alphanumeric
	:	(identifier|INTEGER);

archetypeId
	: (identifier ('.' identifier)* '::')?
	  identifier ('-'|identifier)+ '.' (alphanumeric | '-')+ '.' identifier ('.' INTEGER)*
	  ('-' identifier)?
	;

headerTag
	: ARCHETYPE | TEMPLATE | TEMPLATE_OVERLAY;

header:
	headerTag
    	( '(' archetypePropertyList ')' )?
    	archetypeId
    	;



archetypePropertyList:
	archetypeProperty (';' archetypeProperty)*
	;

archetypeProperty:
	identifier ('=' archetypePropertyValue)?
	;
archetypePropertyValue:
	archetypePropertyValuePart*;

archetypePropertyValuePart:
	~( ';' | ')');

atCode:
	'[' AT_CODE_VALUE ']' ;

specializeTag
	:	SPECIALISE;

specialize:	specializeTag archetypeId;

concept: 	CONCEPT atCode;

language:	LANGUAGE odinObjectValue;

description:	DESCRIPTION odinObjectValue;

definition:	DEFINITION complexObjectConstraint;

typeConstraint
	: orderConstraint? odinValue
	| orderConstraint? complexObjectConstraint
	| orderConstraint? USE_NODE typeIdentifierWithGenerics atCode? occurrences? rmPath
	| orderConstraint? archetypeSlotConstraint
	| orderConstraint? archetypeReferenceConstraint
	;

complexObjectConstraint:
    typeIdentifierWithGenerics atCode? occurrences? attributeListMatcher?;

occurrences:
	OCCURRENCES MATCHES '{' occurrenceRange '}';

occurrenceRange
	: lower=INTEGER '..' upper=INTEGER
	| lower=INTEGER '..' '*'
	| val=INTEGER
	;


attributeListMatcher
	: MATCHES '{' attributeConstraint+ '}'
	| MATCHES '{' '*' '}'
	;

orderConstraint
	: BEFORE atCode
	| AFTER atCode ;


multiValueConstraint
	: '*'
	| valueConstraint+ ;

valueConstraint
	: typeConstraint
	| primitiveValueConstraint
//	| codePhraseConstraint
	| ordinalConstraint
	;

ordinalConstraint:
	ordinalItemList (';' number)? ;
ordinalItemList:
	ordinalItem (',' ordinalItem)*;
ordinalItem:
	number '|' odinCodePhraseValue ;

primitiveValueConstraint
	:  stringConstraint (';' assumedValue=STRING)?
	| numberConstraint (';' number)?
	| booleanList (';' bool)?
	| dateTimeConstraint (';' ISO_DATE_TIME)?
	| dateConstraint (';' ISO_DATE)?
	| timeConstraint (';' ISO_TIME)?
	| durationConstraint (';' DURATION)?
	| terminologyCodeConstraint
//	| '[' codeIdentifierList (';' codeIdentifier)?  ']'
	;

terminologyCodeConstraint:
	'[' constraint=AT_CODE_VALUE (';' assumedValue=AT_CODE_VALUE)? ']';

archetypeSlotConstraint
	: ALLOW_ARCHETYPE typeIdentifierWithGenerics atCode? occurrences? ( MATCHES '{' archetypeSlotValueConstraint '}' )?
	| ALLOW_ARCHETYPE typeIdentifierWithGenerics atCode? CLOSED
	;

archetypeSlotValueConstraint:
	(INCLUDE (include+=archetypeSlotSingleConstraint)*)? (EXCLUDE (exclude+=archetypeSlotSingleConstraint)*)?
	;


archetypeSlotSingleConstraint:
	rmPath MATCHES '{' primitiveValueConstraint '}';


archetypeReferenceConstraint
	: (start=USE_ARCHETYPE|start=USE_TEMPLATE) typeIdentifier '[' AT_CODE_VALUE ',' archetypeId ']' occurrences?
	;


stringConstraint
	: stringList
	| regularExpression;
numberConstraint:
	numberList | numberIntervalConstraint (',' numberIntervalConstraint)*;
dateTimeConstraint:
	DATE_TIME_PATTERN|ISO_DATE_TIME | dateTimeIntervalConstraint (',' dateTimeIntervalConstraint)*;
dateConstraint:
	DATE_PATTERN|ISO_DATE|	dateIntervalConstraint (',' dateIntervalConstraint)*;
timeConstraint:
	TIME_PATTERN|ISO_TIME| timeIntervalConstraint (',' timeIntervalConstraint)*;


durationConstraint
	: (pattern=DURATION) ('/' (durationIntervalConstraint|singleInterval=DURATION))?
	| durationIntervalConstraint
	;


//codePhraseConstraint:
//	'[' tid=codeIdentifier '::' codes=codeIdentifierList (';' assumed=codeIdentifier)? ']' ;

codeIdentifierList:
	codeIdentifier? (',' codeIdentifier)* ;

// value of regular expression must be read directly from tokenStream, since it may contain whitespaces which are on hidden channel
regularExpression
	: '/' regularExpressionInner1 '/'
	| '^' regularExpressionInner2 '^' ;
regularExpressionInner1: ( ~'/')*;
regularExpressionInner2: ( ~'^')*;




attributeConstraint
	: attributeIdentifier existence? cardinality? (MATCHES '{' multiValueConstraint '}' )?
	|  tupleAttributeIdentifier existence? MATCHES '{' tupleChildConstraints '}'
	;

tupleAttributeIdentifier
	: '[' attributeIdentifier (',' attributeIdentifier)* ']'
	;

tupleChildConstraints
	: tupleChildConstraint (',' tupleChildConstraint)*;

tupleChildConstraint
	: '[' '{' primitiveValueConstraint '}' (',' '{' primitiveValueConstraint '}')* ']'
;

attributeIdentifier:
	rmPath;


existence:
	EXISTENCE MATCHES '{' occurrenceRange '}' ;


cardinality:
	CARDINALITY MATCHES '{' occurrenceRange (';' (ord=ORDERED|ord=UNORDERED))? (';' (uq=UNIQUE))? '}';



numberIntervalConstraint
	// range
    : '|'  (gt='>')? (lower=number) '..' (lt='<')? (upper=number) '|'
	// relative to single value
	| '|' ( (gt='>' (gte='=')?)? | (lt='<' (lte='=')?)? ) val=number '|'
	;

dateIntervalConstraint
	// range
    : '|'  (gt='>')? (lower=ISO_DATE) '..' (lt='<')? (upper=ISO_DATE) '|'
	// relative to single value
	| '|' ( (gt='>' (gte='=')?)? | (lt='<' (lte='=')?)? ) val=ISO_DATE '|'
	;

timeIntervalConstraint
	// range
    : '|'  (gt='>')? (lower=ISO_TIME) '..' (lt='<')? (upper=ISO_TIME) '|'
	// relative to single value
	| '|' ( (gt='>' (gte='=')?)? | (lt='<' (lte='=')?)? ) val=ISO_TIME '|'
	;

dateTimeIntervalConstraint
	// range
    : '|'  (gt='>')? (lower=ISO_DATE_TIME) '..' (lt='<')? (upper=ISO_DATE_TIME) '|'
	// relative to single value
	| '|' ( (gt='>' (gte='=')?)? | (lt='<' (lte='=')?)? ) val=ISO_DATE_TIME '|'
	;

durationIntervalConstraint
	// range
    : '|'  (gt='>')? (lower=DURATION|'*') '..' (lt='<')? (upper=DURATION|'*') '|'
	// relative to single value
	| '|' ( (gt='>' (gte='=')?)? | (lt='<' (lte='=')?)? ) val=DURATION '|'
	;


terminology:	TERMINOLOGY odinObjectValue;
annotations:	ANNOTATIONS odinObjectValue;

// Odin dadl structure
odinValue
	: '<'  ( odinMapValue | odinCodePhraseValueList |  openStringList | numberIntervalConstraint ) '>' ';'?
	| (('(' typeIdentifier ')') | typeIdentifier)? '<' odinObjectValue? '>' ';'?
	| '<' number '>'
	| '<' url '>'
	;

odinObjectValue:
	( odinObjectProperty ) + ;
odinObjectProperty:
	identifier '=' odinValue ;

odinCodePhraseValueList:
	odinCodePhraseValue (',' odinCodePhraseValue)* ;
odinCodePhraseValue
	: '[' tid=codeIdentifier '::' code=codeIdentifier ']'
	;
odinMapValue:
	( odinMapValueEntry )+ ;
odinMapValueEntry:
	'[' key=STRING ']' '=' value=odinValue  ;

openStringList:
	STRING (',' STRING)* (',' '...')? ;

stringList:	STRING (',' STRING)* ;

numberList:	number (',' number)* ;
booleanList:	bool (',' bool)* ;


codeIdentifier:
		codeIdentifierPart+ ;
codeIdentifierPart:
		(identifier | number |ISO_DATE_TIME | ISO_DATE | '.'| '-' | '(' | ')' | ':' | '/') ;

identifier:
	nameIdentifier | typeIdentifier;

bool:
	(TRUE | FALSE);
nameIdentifier:
	NAME_IDENTIFIER | keyword | AT_CODE_VALUE;

typeIdentifierWithGenerics:
	mainType=typeIdentifier ('<' genericType=typeIdentifier '>')? ;

typeIdentifier:
	TYPE_IDENTIFIER ;

keyword
	: LANGUAGE | ARCHETYPE | DESCRIPTION | CONCEPT | TERMINOLOGY
	| DEFINITION | MATCHES | USE_NODE | OCCURRENCES | CARDINALITY | ORDERED
	| UNORDERED | EXISTENCE | CLOSED | ANNOTATIONS | TEMPLATE | TEMPLATE_OVERLAY
	| SPECIALISE | UNIQUE
	;


pathSegment: nameIdentifier atCode? ;
rmPath: '/'? pathSegment ('/' pathSegment)* ;


url:
	identifier ':' (codeIdentifierPart|'_'|'.'|'='|'?'|'-'|'/'|'&'|';'| UNICODE_CHAR)+ ;

numberOrStar:
	number | '*';

number:
	'-'? INTEGER ('.' INTEGER)?;








// lexer

RANGE:		    '..';

EQUALS:        	'=';
PAREN_OPEN:    	'(';
PAREN_CLOSE:   	')';
BRACKET_OPEN:  	'[';
BRACKET_CLOSE: 	']';
GT:		        '>';
LT:		        '<';
PERIOD:        	'.';
COMMA:     	    ',';
COLON:     	    ':';
SEMICOLON:     	';';
MINUS:    	    '-';
UNDERSCORE:    	'_';
PIPE:		    '|';
ASTERISK:	    '*';
CARET:		    '^';
AMP:		    '&';
EXCLAMATION:	'!';
QUESTION:	    '?';

// all other symbols.
OTHER_SYMBOL:	'!'..'/' | ':'..'@' | '['..'`' | '{'..'~';

fragment  LOWERCASE: 	'a'..'z';
fragment  UPPERCASE:	'A'..'Z';
fragment  LETTER:	LOWERCASE | UPPERCASE;
fragment  DIGIT:	'0'..'9';
fragment  ALPHANUM:	LETTER | DIGIT;

// date time pattern
fragment YEAR_PATTERN:	 		('yyy' 'y'?) | ('YYY' 'Y'?);
fragment MONTH_OR_MINUTE_PATTERN:	'mm' | 'MM' | '??' | 'XX';
fragment DAY_PATTERN:			'dd' | 'DD' | '??' | 'XX';
fragment HOUR_PATTERN:			'hh' | 'HH' | '??' | 'XX';
fragment SECOND_PATTERN:		'ss' | 'SS' | '??' | 'XX';

DATE_TIME_PATTERN:			DATE_PATTERN 'T' TIME_PATTERN;
TIME_PATTERN:				HOUR_PATTERN ':' MONTH_OR_MINUTE_PATTERN ':' SECOND_PATTERN;
DATE_PATTERN:				YEAR_PATTERN '-' MONTH_OR_MINUTE_PATTERN '-' DAY_PATTERN;

fragment	DURATION_Y:		'Y'|'y';
fragment	DURATION_M:		'M'|'m';
fragment	DURATION_W:		'W'|'w';
fragment	DURATION_D:		'D'|'d';
fragment	DURATION_H:		'H'|'h';
fragment	DURATION_S:		'S'|'s';


// Can represent either a period or pattern (depending on whether there are any NUMBERS in the value)
DURATION: 	'P' (POSITIVE_FLOAT? DURATION_Y)? (POSITIVE_FLOAT? DURATION_M)? (POSITIVE_FLOAT? DURATION_W)? (POSITIVE_FLOAT? DURATION_D)?
		('T' (POSITIVE_FLOAT? DURATION_H)? (POSITIVE_FLOAT? DURATION_M)? (POSITIVE_FLOAT? DURATION_S)?)?  ;


fragment ISO_TIMEZONE:		'Z' | ('+'|'-') DIGIT DIGIT DIGIT DIGIT ;


// same as ISO_TIME, but allows to skip minutes as well, which is only possible in full datetime
fragment ISO_TIME_PART:		DIGIT DIGIT ( (':' DIGIT DIGIT) ( (':' DIGIT DIGIT) (',' DIGIT (DIGIT (DIGIT DIGIT?)?)? )? )? )? ISO_TIMEZONE?;

ISO_DATE_TIME :     YEAR '-' MONTH '-' DAY 'T' HOUR (':' MINUTE (':' SECOND ( ',' DIGIT+ )?)?)? ( TIMEZONE )? ;
ISO_DATE      :     YEAR '-' MONTH ( '-' DAY )? ;
ISO_TIME      :     HOUR ':' MINUTE ( ':' SECOND ( ',' INTEGER )?)? ( TIMEZONE )? ;

fragment TIMEZONE   :     'Z' | ('+'|'-') DIGIT DIGIT DIGIT DIGIT ;   // hour offset, e.g. `+0930`, or else literal `Z` indicating +0000.
fragment YEAR       :     DIGIT DIGIT DIGIT DIGIT ;
fragment MONTH      :     ( '0' DIGIT | '1' '0'..'2' ) ;    // month in year
fragment DAY        :     ( '0'..'2' DIGIT | '3' '0'..'2' ) ;  // day in month
fragment HOUR       :     ( ('0'..'1')? DIGIT | '2' '0'..'3' ) ;  // hour in 24 hour clock
fragment MINUTE     :     '0'..'5' DIGIT ;                 // minutes
fragment SECOND     :     '0'..'5' DIGIT ;                 // seconds
fragment HOUR_MIN   :     HOUR ':' MINUTE ;

AT_CODE_VALUE: 		('at'|'ac'|'id') DIGIT+ (PERIOD DIGIT+)* ;

// keywords.
LANGUAGE: 		    L A N G U A G E;
ARCHETYPE:		    A R C H E T Y P E;
TEMPLATE:		    T E M P L A T E;
TEMPLATE_OVERLAY:	T E M P L A T E '_' O V E R L A Y;
CONCEPT:		    C O N C E P T;
DESCRIPTION:		D E S C R I P T I O N;
SPECIALISE:		    S P E C I A L I (S|Z) E;
DEFINITION:		    D E F I N I T I O N;
MATCHES:		    M A T C H E S;
USE_NODE:		    U S E '_' N O D E;
OCCURRENCES:		O C C U R R E N C E S;
INCLUDE:		    I N C L U D E;
EXCLUDE:		    E X C L U D E;
ALLOW_ARCHETYPE:	A L L O W '_' A R C H E T Y P E  ;
CARDINALITY:		C A R D I N A L I T Y;
UNORDERED:		    U N O R D E R E D;
ORDERED:		    O R D E R E D;
UNIQUE:			    U N I Q U E;
EXISTENCE:		    E X I S T E N C E;
BEFORE:			    B E F O R E;
AFTER:			    A F T E R;
USE_ARCHETYPE:		U S E '_' A R C H E T Y P E;
USE_TEMPLATE:		U S E '_' T E M P L A T E;
CLOSED:			    C L O S E D;
ANNOTATIONS:		A N N O T A T I O N S;
TERMINOLOGY:		T E R M I N O L O G Y ;



TRUE:		T R U E;
FALSE:		F A L S E;

fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');


TYPE_IDENTIFIER:     	(UPPERCASE) (LETTER | DIGIT | UNDERSCORE)* ;
NAME_IDENTIFIER:     	(LOWERCASE|UNDERSCORE) (LETTER | DIGIT | UNDERSCORE)* ;


INTEGER:	DIGIT+;

fragment POSITIVE_FLOAT:	DIGIT+ ('.' DIGIT+)? ;



fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;


fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;
fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;


STRING
    	:  '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\''
    	|  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    	;


// matches all unicode characters
UNICODE_CHAR: '\u0080'..'\uFFFE' ;


fragment IDCHAR:	ALPHANUM| '_' ;



LINE_COMMENT: 	'--' (~'\n')*? '\n' -> skip ;
WS:   		( ' ' | '\t' | '\r' | '\n') -> skip ;
