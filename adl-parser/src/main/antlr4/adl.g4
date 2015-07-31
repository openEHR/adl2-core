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


adl: header specialize? concept? language? description? definition? ontology? annotations? ;

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
	:	SPECIALIZE | SPECIALISE;

specialize:	specializeTag archetypeId;

concept: 	CONCEPT atCode;

language:	LANGUAGE adlObjectValue;

description:	DESCRIPTION adlObjectValue;

// Constraint definition
definition:	DEFINITION complexObjectConstraint;

typeConstraint
	: orderConstraint? complexObjectConstraint
	| orderConstraint? USE_NODE typeIdentifierWithGenerics atCode? occurrences? rmPath
	| orderConstraint? archetypeSlotConstraint
	| orderConstraint? archetypeReferenceConstraint
	| orderConstraint? adlValueConstraint
	;

complexObjectConstraint:
    typeIdentifierWithGenerics atCode? occurrences? attributeListMatcher?;

adlValueConstraint
	: typeIdentifier adlValue
	| '(' typeIdentifier ')' adlValue
	;
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
	| codePhraseConstraint
	| ordinalConstraint
	| constraintRef
	;

constraintRef:
	'[' AT_CODE_VALUE ']';
ordinalConstraint:
	ordinalItemList (';' number)? ;
ordinalItemList:
	ordinalItem (',' ordinalItem)*;
ordinalItem:
	number '|' adlCodePhraseValue ;

// maybe there is a possible valueConstraint inside <>
/*
typeDefinitionConstraint:
	typeIdentifier adlValue -> ^(AST_TYPE_DEFINITION_CONSTRAINT typeIdentifier adlValue) ;
*/

primitiveValueConstraint
	:  stringConstraint (';' STRING)?
	| numberConstraint (';' number)?
	| booleanList (';' bool)?
	| dateTimeConstraint (';' ISO_DATE_TIME)?
	| dateConstraint (';' ISO_DATE)?
	| timeConstraint (';' ISO_TIME)?
	| durationConstraint (';' DURATION)?
	| '[' codeIdentifierList (';' codeIdentifier)?  ']'
	;

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
	: (start=USE_ARCHETYPE|start=USE_TEMPLATE) typeIdentifier '[' (AT_CODE_VALUE ',')? archetypeId ']' occurrences? attributeListMatcher?
	;


stringConstraint
	: stringList
	| regularExpression;
numberConstraint:
	numberList|numberIntervalConstraint;
dateTimeConstraint:
	DATE_TIME_PATTERN|ISO_DATE_TIME|dateTimeIntervalConstraint;
dateConstraint:
	DATE_PATTERN|ISO_DATE|dateIntervalConstraint;
timeConstraint:
	TIME_PATTERN|ISO_TIME|timeIntervalConstraint;


durationConstraint
	: DURATION '/' durationIntervalConstraint
	| DURATION
	| durationIntervalConstraint
	;


codePhraseConstraint:
	'[' tid=codeIdentifier '::' codeIdentifierList (';' assumed=codeIdentifier)? ']' ;

codeIdentifierList:
	codeIdentifier? (',' codeIdentifier)* ;

// value of regular expression must be read directly from tokenStream, since it may contain whitespaces which are on hidden channel
// trying to rewrite AST with embedded inner part throws antlr InternalException
regularExpression
	: '/' regularExpressionInner1 '/'
	| '^' regularExpressionInner2 '^' ;
regularExpressionInner1: ( ~'/')*;
regularExpressionInner2: ( ~'^')*;







attributeConstraint
	: attributeIdentifier existence? cardinality? ((negatedMatches='~')? MATCHES '{' multiValueConstraint '}' )?
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





numberIntervalConstraint:
	// range
	'|'  ('>' '=')? numberOrStar '..' ('<' '=')? numberOrStar '|' |
	'|'  '>'        numberOrStar '..' ('<' '=')? numberOrStar '|' |
	'|'  ('>' '=')? numberOrStar '..' '<'        numberOrStar '|' |
	'|'  '>'        numberOrStar '..' '<'        numberOrStar '|' |
	// relative to single value
	'|' val=      number '|'  |
	'|' ('>' '=') number '|'  |
	'|' '>'       number '|'  |
	'|' ('<' '=') number '|'  |
	'|' '<'       number '|'
	;
dateIntervalConstraint:
	// range
	'|'  ('>' '=')? dateOrStar '..' ('<' '=')? dateOrStar '|'  |
	'|'  '>'        dateOrStar '..' ('<' '=')? dateOrStar '|'  |
	'|'  ('>' '=')? dateOrStar '..' '<'        dateOrStar '|'  |
	'|'  '>'        dateOrStar '..' '<'        dateOrStar '|'  |
	// relative to single value
	'|' val=      ISO_DATE '|'  |
	'|' ('>' '=') ISO_DATE '|'  |
	'|' '>'       ISO_DATE '|'  |
	'|' ('<' '=') ISO_DATE '|'  |
	'|' '<'       ISO_DATE '|'
	;

dateOrStar
	:		ISO_DATE | '*';

timeIntervalConstraint:
	// range
	'|'  ('>' '=')? timeOrStar '..' ('<' '=')? timeOrStar '|'  |
	'|'  '>'        timeOrStar '..' ('<' '=')? timeOrStar '|'  |
	'|'  ('>' '=')? timeOrStar '..' '<'        timeOrStar '|'  |
	'|'  '>'        timeOrStar '..' '<'        timeOrStar '|'  |
	// relative to single value
	'|' val=      ISO_TIME '|'  |
	'|' ('>' '=') ISO_TIME '|'  |
	'|' '>'       ISO_TIME '|'  |
	'|' ('<' '=') ISO_TIME '|'  |
	'|' '<'       ISO_TIME '|'
	;

timeOrStar
	:	ISO_TIME | '*';

dateTimeIntervalConstraint:
	// range
	'|'  ('>' '=')? dateTimeOrStar '..' ('<' '=')? dateTimeOrStar '|'  |
	'|'  '>'        dateTimeOrStar '..' ('<' '=')? dateTimeOrStar '|'  |
	'|'  ('>' '=')? dateTimeOrStar '..' '<'        dateTimeOrStar '|'  |
	'|'  '>'        dateTimeOrStar '..' '<'        dateTimeOrStar '|'  |
	// relative to single value
	'|' val=      ISO_DATE_TIME '|'  |
	'|' ('>' '=') ISO_DATE_TIME '|'  |
	'|' '>'       ISO_DATE_TIME '|'  |
	'|' ('<' '=') ISO_DATE_TIME '|'  |
	'|' '<'       ISO_DATE_TIME '|'
	;

dateTimeOrStar
	:	ISO_DATE_TIME|'*';

durationIntervalConstraint:
	// range
	'|'  ('>' '=')? durationOrStar '..' ('<' '=')? durationOrStar '|'  |
	'|'  '>'        durationOrStar '..' ('<' '=')? durationOrStar '|'  |
	'|'  ('>' '=')? durationOrStar '..' '<'        durationOrStar '|'  |
	'|'  '>'        durationOrStar '..' '<'        durationOrStar '|'  |
	// relative to single value
	'|' val=        DURATION '|'  |
	'|' ('>' '=')   DURATION '|'  |
	'|' '>'         DURATION '|'  |
	'|' ('<' '=')   DURATION '|'  |
	'|' '<'         DURATION '|'
	;

durationOrStar
	:	DURATION|'*';




// Ontology
ontology:	(ONTOLOGY|TERMINOLOGY) adlObjectValue;
annotations:	ANNOTATIONS adlObjectValue;

// Adl structure
adlValue
	: '<'  ( adlMapValue | adlCodePhraseValueList |  openStringList | adlObjectValue | numberIntervalConstraint ) '>' ';'?
	| '<' number '>'
	| '<' url '>'
	| '<' '>'
	;

adlObjectValue:
	( adlObjectProperty ) + ;
adlObjectProperty:
	identifier '=' adlValue ;

adlCodePhraseValueList:
	adlCodePhraseValue (',' adlCodePhraseValue)* ;
adlCodePhraseValue
	: '[' tid=codeIdentifier '::' code=codeIdentifier ']'
	;
adlMapValue:
	( adlMapValueEntry )+ ;
adlMapValueEntry:
	'[' STRING ']' '=' adlValue  ;

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
	: LANGUAGE | ARCHETYPE | ONTOLOGY | DESCRIPTION | CONCEPT | SPECIALIZE
	| DEFINITION | MATCHES | USE_NODE | OCCURRENCES | CARDINALITY | ORDERED
	| UNORDERED | EXISTENCE | CLOSED | ANNOTATIONS | TEMPLATE | TEMPLATE_OVERLAY
	| SPECIALISE | UNIQUE | TERMINOLOGY
	;


pathSegment: nameIdentifier atCode? ;
rmPath: '/'? pathSegment ('/' pathSegment)* ;


url:
	identifier ':' (codeIdentifierPart|'_'|'.'|'='|'?'|'-'|'/'|'&'|';'| UNICODE_CHAR)+ ;

numberOrStar:
	number | '*';

number:
	'-'? INTEGER ('.' INTEGER)?;










RANGE:		'..';

EQUALS:        	'=';
PAREN_OPEN:    	'(';
PAREN_CLOSE:   	')';
BRACKET_OPEN:  	'[';
BRACKET_CLOSE: 	']';
GT:		'>';
LT:		'<';
PERIOD:        	'.';
COMMA:     	',';
COLON:     	':';
SEMICOLON:     	';';
MINUS:    	'-';
UNDERSCORE:    	'_';
PIPE:		'|';
ASTERISK:	'*';
CARET:		'^';
AMP:		'&';
EXCLAMATION:	'!';
QUESTION:	'?';

// all other symbols. These are defined to allow any symbol inside regular expressions
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

DATE_TIME_PATTERN:			DATE_PATTERN ('T' | ' ') TIME_PATTERN;
TIME_PATTERN:				HOUR_PATTERN ':' MONTH_OR_MINUTE_PATTERN (':' SECOND_PATTERN)?;
DATE_PATTERN:				YEAR_PATTERN '-' MONTH_OR_MINUTE_PATTERN ('-' DAY_PATTERN)?;

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

fragment TIMEZONE   :     'Z' | ('+'|'-') HOUR_MIN ;   // hour offset, e.g. `+0930`, or else literal `Z` indicating +0000.
fragment YEAR       :     DIGIT DIGIT DIGIT DIGIT ;
fragment MONTH      :     ( '0' DIGIT | '1' '1'..'2' ) ;    // month in year
fragment DAY        :     ( '0'..'2' DIGIT | '3' '0'..'2' ) ;  // day in month
fragment HOUR       :     ( ('0'..'1')? DIGIT | '2' '0'..'3' ) ;  // hour in 24 hour clock
fragment MINUTE     :     '0'..'5' DIGIT ;                 // minutes
fragment SECOND     :     '0'..'5' DIGIT ;                 // seconds
fragment HOUR_MIN   :     HOUR ':' MINUTE ;

/*
ARCHETYPEID
	:	LETTER+ '-' LETTER+ '-' (LETTER|'_')+ '.' (IDCHAR|'-')+ '.v' DIGIT+ ('.' DIGIT+)?
	;
*/
AT_CODE_VALUE: 		('at'|'ac'|'id') DIGIT+ (PERIOD DIGIT+)* ;

// keywords. Some of these must also be added to keyword target. Must start with lowercase or '_'
LANGUAGE: 		'language';
ARCHETYPE:		'archetype';
TEMPLATE:		'template';
TEMPLATE_OVERLAY:	'template_overlay';
CONCEPT:		'concept';
DESCRIPTION:		'description';
ONTOLOGY:		'ontology';
SPECIALIZE:		'specialize';
SPECIALISE:		'specialise';
DEFINITION:		'definition';
MATCHES:		'matches';
USE_NODE:		'use_node';
OCCURRENCES:		'occurrences';
INCLUDE:		'include';
EXCLUDE:		'exclude';
ALLOW_ARCHETYPE:	'allow_archetype';
CARDINALITY:		'cardinality';
UNORDERED:		'unordered';
ORDERED:		'ordered';
UNIQUE:			'unique';
EXISTENCE:		'existence';
BEFORE:			'before';
AFTER:			'after';
USE_ARCHETYPE:		'use_archetype';
USE_TEMPLATE:		'use_template';
CLOSED:			'closed';
ANNOTATIONS:		'annotations';
TERMINOLOGY:		'terminology';



// types. all these must also be added to typeIdentifier target. Must start with uppercase
TRUE:		'True';
FALSE:		'False';


TYPE_IDENTIFIER:     	(UPPERCASE) (LETTER | DIGIT | UNDERSCORE)* ;
NAME_IDENTIFIER:     	(LOWERCASE|UNDERSCORE) (LETTER | DIGIT | UNDERSCORE)* ;


// Simple number definition messes up intervals, even though '..' should not match '.'
/*
NUMBER
	: (INT '.' '.')=> INT
	| (DOUBLE)=> DOUBLE
	| INT;
*/
INTEGER:	DIGIT+;

fragment POSITIVE_FLOAT:	DIGIT+ ('.' DIGIT+)? ;



//NUMBER:			'-'? DIGIT+ ('.'? (DIGIT+))?;


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
