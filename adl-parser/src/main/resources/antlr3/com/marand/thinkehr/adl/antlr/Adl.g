grammar Adl;

options {
	output=AST;
	ASTLabelType=CommonTree;
	backtrack=true;
	memoize=true;
}

tokens {
	// imaginary tokens used only for AST tree
	AST_ARCHETYPE_PROPERTIES;
	AST_ARCHETYPE_PROPERTY;
	AST_ARCHETYPE_ID;
	AST_ADL_OBJECT;
	AST_ADL_OBJECT_PROPERTY;
	AST_CODE_PHRASE;
	AST_CODE_PHRASE_CONSTRAINT;
	AST_CODE_PHRASE_LIST;
	AST_CODE_LIST;
	AST_TEXT;
	AST_NUMBER;
	AST_PATH;
	AST_ADL_MAP;
	AST_ADL_MAP_ENTRY;
	AST_STRING_LIST;
	AST_NUMBER_LIST;
	AST_BOOLEAN_LIST;
	AST_LIST; 		// use when type is known to avoid defining lists fo each needed type
	AST_REGULAR_EXPRESSION;
	AST_TYPE_CONSTRAINT;
	AST_CONSTRAINT_ANY;
	AST_ATTRIBUTE_CONSTRAINT_LIST;
	AST_ATTRIBUTE_CONSTRAINT;
	AST_ATTRIBUTE_SECOND_ORDER_CONSTRAINT;
	AST_DURATION_CONSTRAINT; // (pattern|value)% interval%
	AST_USE_NODE_CONSTRAINT;
	AST_ORDINAL_CONSTRAINT;
	AST_CONSTRAINT_REF;
	AST_ORDINAL_ITEM_CONSTRAINT;
	AST_PRIMITIVE_VALUE_CONSTRAINT; // constraint assumedValue?
	AST_ASSUMED_VALUE_CONSTRAINT;
	AST_NUMBER_INTERVAL_CONSTRAINT; // low%, high%, lowIncluded, highIncluded
	AST_DATE_TIME_INTERVAL_CONSTRAINT; // low%, high%, lowIncluded, highIncluded
	AST_DATE_INTERVAL_CONSTRAINT; // low%, high%, lowIncluded, highIncluded
	AST_TIME_INTERVAL_CONSTRAINT; // low%, high%, lowIncluded, highIncluded
	AST_DURATION_INTERVAL_CONSTRAINT; // low%, high%, lowIncluded, highIncluded
	AST_ARCHETYPE_SLOT_CONSTRAINT; // rmType atCode? occurrences? AST_ARCHETYPE_SLOT_VALUE_CONSTRAINT
	AST_ARCHETYPE_SLOT_VALUE_CONSTRAINT; // INCLUDE EXCLUDE?
	AST_ARCHETYPE_SLOT_SINGLE_CONSTRAINT;
	AST_ADL_VALUE_CONSTRAINT;
	AST_NULL;
	
	AST_NUMBER_CONTAINER;
	AST_TEXT_CONTAINER;
	AST_COPY;
}

@header {
package com.marand.thinkehr.adl.antlr;
}

@lexer::header{
package com.marand.thinkehr.adl.antlr;
}


adl: header specialize? concept? language? description? definition? ontology? annotations? EOF! ;
	
archetypeId
	: (identifier ('.' identifier)* '::')?
	  identifier ('-'|identifier)+ '.' (identifier|'-')+ '.' identifier ('.' INTEGER)* 
	  ('-' identifier)? -> AST_ARCHETYPE_ID
	
	;

headerTag
	: ARCHETYPE | TEMPLATE | TEMPLATE_OVERLAY;
	
header:	 
	headerTag
    	( '(' archetypePropertyList ')' )?
    	archetypeId
    	-> ^(headerTag archetypeId archetypePropertyList? )
    	; 
	
    	
             
archetypePropertyList:	
	archetypeProperty (';' archetypeProperty)* 
	-> ^(AST_ARCHETYPE_PROPERTIES archetypeProperty+)
	;

archetypeProperty:	
	identifier ('=' val=archetypePropertyValue)?
	-> ^(AST_ARCHETYPE_PROPERTY identifier archetypePropertyValue?)
	;
archetypePropertyValue:
	archetypePropertyValuePart* -> ^(AST_TEXT archetypePropertyValuePart*);
archetypePropertyValuePart:	
	~( ';' | ')');
    
atCode:	
	'['! AT_CODE_VALUE ']'! ;

specializeTag
	:	SPECIALIZE | SPECIALISE;
specialize:	specializeTag ^archetypeId;

concept: 	CONCEPT ^atCode;

language:	LANGUAGE ^adlObjectValue;

description:	DESCRIPTION ^adlObjectValue;

// Constraint definition
definition:	DEFINITION ^typeConstraint;

typeConstraint
	: orderConstraint? typeIdentifierWithGenerics atCode? occurrences? typeValueConstraint
		-> ^(AST_TYPE_CONSTRAINT orderConstraint? typeIdentifierWithGenerics atCode? occurrences? typeValueConstraint) 
	| USE_NODE typeIdentifierWithGenerics atCode? occurrences? rmPath 
		-> ^(AST_USE_NODE_CONSTRAINT USE_NODE typeIdentifierWithGenerics atCode? occurrences? rmPath)
	| archetypeSlotConstraint
	| archetypeReferenceConstraint
	| adlValueConstraint
	;
	
adlValueConstraint
	: typeIdentifier adlValue -> ^(AST_ADL_VALUE_CONSTRAINT typeIdentifier adlValue)
	| '(' typeIdentifier ')' adlValue -> ^(AST_ADL_VALUE_CONSTRAINT typeIdentifier adlValue)
	;
occurrences:
	OCCURRENCES MATCHES '{' occurrenceRange '}' -> ^(OCCURRENCES occurrenceRange);
	
occurrenceRange
	: INTEGER '..' INTEGER 	-> ^(AST_NUMBER_INTERVAL_CONSTRAINT INTEGER INTEGER TRUE TRUE)
	| INTEGER '..' '*' 	-> ^(AST_NUMBER_INTERVAL_CONSTRAINT INTEGER AST_NULL TRUE FALSE)
	| val=INTEGER		-> ^(AST_NUMBER_INTERVAL_CONSTRAINT $val $val TRUE TRUE)
	;
	
negatableMatches
	: '~' MATCHES 	-> ^(MATCHES FALSE)
	| MATCHES 	-> ^(MATCHES TRUE)
	;

typeValueConstraint
	: MATCHES '{' attributeListConstraint '}' -> attributeListConstraint
	| MATCHES '{' '*' '}' -> AST_CONSTRAINT_ANY
	| -> AST_CONSTRAINT_ANY
	;
		
orderConstraint
	: BEFORE ^atCode
	| AFTER ^atCode ;

		
multiValueConstraint
	: '*' -> AST_CONSTRAINT_ANY 
	| valueConstraint+ ;
	
valueConstraint
	: typeConstraint
	| primitiveValueConstraint 
	| codePhraseConstraint 
	| ordinalConstraint 
	| constraintRef
	;

constraintRef:
	'[' AT_CODE_VALUE ']' -> ^(AST_CONSTRAINT_REF AT_CODE_VALUE) ;
ordinalConstraint:
	ordinalItemList (';' number)? -> ^(AST_ORDINAL_CONSTRAINT ordinalItemList ^(AST_ASSUMED_VALUE_CONSTRAINT number)?) ;
ordinalItemList:
	ordinalItem (',' ordinalItem)* -> ^(AST_LIST ordinalItem+) ;
ordinalItem:
	number '|' adlCodePhraseValue -> ^(AST_ORDINAL_ITEM_CONSTRAINT number adlCodePhraseValue);
	
// maybe there is a possible valueConstraint inside <>
/*
typeDefinitionConstraint:
	typeIdentifier adlValue -> ^(AST_TYPE_DEFINITION_CONSTRAINT typeIdentifier adlValue) ;
*/	

primitiveValueConstraint
	:  stringConstraint (';' STRING)? 
		-> ^(AST_PRIMITIVE_VALUE_CONSTRAINT stringConstraint ^(AST_ASSUMED_VALUE_CONSTRAINT STRING)?) 
	| numberConstraint (';' number)? 
		-> ^(AST_PRIMITIVE_VALUE_CONSTRAINT numberConstraint ^(AST_ASSUMED_VALUE_CONSTRAINT number)?) 
	| booleanList (';' bool)? 
		-> ^(AST_PRIMITIVE_VALUE_CONSTRAINT booleanList ^(AST_ASSUMED_VALUE_CONSTRAINT bool)?) 
	| dateTimeConstraint (';' ISO_DATE_TIME)?
		-> ^(AST_PRIMITIVE_VALUE_CONSTRAINT dateTimeConstraint ^(AST_ASSUMED_VALUE_CONSTRAINT ISO_DATE_TIME)?) 
	| dateConstraint (';' ISO_DATE)?
		-> ^(AST_PRIMITIVE_VALUE_CONSTRAINT dateConstraint ^(AST_ASSUMED_VALUE_CONSTRAINT ISO_DATE)?) 
	| timeConstraint (';' ISO_TIME)?
		-> ^(AST_PRIMITIVE_VALUE_CONSTRAINT timeConstraint ^(AST_ASSUMED_VALUE_CONSTRAINT ISO_TIME)?) 
	| durationConstraint (';' DURATION)?
		-> ^(AST_PRIMITIVE_VALUE_CONSTRAINT durationConstraint ^(AST_ASSUMED_VALUE_CONSTRAINT DURATION)?) 
	| '[' codeIdentifierList (';' codeIdentifier)?  ']'
		-> ^(AST_PRIMITIVE_VALUE_CONSTRAINT codeIdentifierList ^(AST_ASSUMED_VALUE_CONSTRAINT codeIdentifier)?) 
	;
	
archetypeSlotConstraint
	: ALLOW_ARCHETYPE typeIdentifierWithGenerics atCode? occurrences? ( MATCHES '{' archetypeSlotValueConstraint '}' )?
		-> ^(AST_ARCHETYPE_SLOT_CONSTRAINT typeIdentifierWithGenerics atCode? occurrences? archetypeSlotValueConstraint?)
	| ALLOW_ARCHETYPE typeIdentifierWithGenerics atCode? CLOSED
		-> ^(AST_ARCHETYPE_SLOT_CONSTRAINT typeIdentifierWithGenerics atCode? CLOSED)
	;
		
archetypeSlotValueConstraint:
	(INCLUDE (include+=archetypeSlotSingleConstraint)*)? (EXCLUDE (exclude+=archetypeSlotSingleConstraint)*)?
	-> ^(AST_ARCHETYPE_SLOT_VALUE_CONSTRAINT ^(INCLUDE $include*)? ^(EXCLUDE $exclude*)?)	
	;
	
	
archetypeSlotSingleConstraint:
	rmPath MATCHES '{' primitiveValueConstraint '}' -> ^(AST_ARCHETYPE_SLOT_SINGLE_CONSTRAINT rmPath primitiveValueConstraint);
	

archetypeReferenceConstraint
	: (start=USE_ARCHETYPE|start=USE_TEMPLATE) typeIdentifier '[' (AT_CODE_VALUE ',')? archetypeId ']' occurrences? typeValueConstraint
		-> ^($start typeIdentifier AT_CODE_VALUE? archetypeId occurrences? typeValueConstraint)
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
	: DURATION '/' durationIntervalConstraint -> ^(AST_DURATION_CONSTRAINT DURATION durationIntervalConstraint)
	| DURATION -> ^(AST_DURATION_CONSTRAINT DURATION AST_NULL)
	| durationIntervalConstraint -> ^(AST_DURATION_CONSTRAINT AST_NULL durationIntervalConstraint)
	;
	

codePhraseConstraint:
	'[' tid=codeIdentifier '::' codeIdentifierList (';' assumed=codeIdentifier)? ']' -> ^(AST_CODE_PHRASE_CONSTRAINT $tid codeIdentifierList ^(AST_ASSUMED_VALUE_CONSTRAINT $assumed)?);

codeIdentifierList:
	codeIdentifier? (',' codeIdentifier)* -> ^(AST_CODE_LIST codeIdentifier*);

// value of regular expression must be read directly from tokenStream, since it may contain whitespaces which are on hidden channel
// trying to rewrite AST with embedded inner part throws antlr InternalException
regularExpression
	: '/' regularExpressionInner1 '/' -> ^(AST_REGULAR_EXPRESSION regularExpressionInner1) 
	| '^' regularExpressionInner2 '^' -> ^(AST_REGULAR_EXPRESSION regularExpressionInner2);
regularExpressionInner1: ( ~'/')*;
regularExpressionInner2: ( ~'^')*;
	
	
	
	

attributeListConstraint:
	 attributeConstraint+ -> ^(AST_ATTRIBUTE_CONSTRAINT_LIST attributeConstraint+ );
	
	
attributeConstraint
	: attributeIdentifier existence? cardinality? (negatableMatches '{' multiValueConstraint '}' )?
		-> ^(AST_ATTRIBUTE_CONSTRAINT attributeIdentifier existence? cardinality? negatableMatches? multiValueConstraint? )
	|  tupleAttributeIdentifier existence? MATCHES '{' tupleChildConstraints '}' 
		-> ^(AST_ATTRIBUTE_SECOND_ORDER_CONSTRAINT tupleAttributeIdentifier existence? tupleChildConstraints)
	;
		
tupleAttributeIdentifier
	: '[' attributeIdentifier (',' attributeIdentifier)* ']' -> ^(AST_LIST attributeIdentifier+) 
	;
		
tupleChildConstraints
	: tupleChildConstraint (',' tupleChildConstraint)* -> ^(AST_LIST tupleChildConstraint+);

tupleChildConstraint
	: '[' '{' primitiveValueConstraint '}' (',' '{' primitiveValueConstraint '}')* ']'
		-> ^(AST_LIST primitiveValueConstraint+);

attributeIdentifier:
	rmPath;
	

existence:
	EXISTENCE MATCHES '{' occurrenceRange '}' -> ^(EXISTENCE occurrenceRange);

	
cardinality:
	CARDINALITY MATCHES '{' occurrenceRange (';' (ord=ORDERED|ord=UNORDERED))? '}' -> ^(CARDINALITY occurrenceRange $ord?);

	
	
	

numberIntervalConstraint:
	// range
	'|'  ('>' '=')? number '..' ('<' '=')? number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT number number TRUE TRUE) |
	'|'  '>'        number '..' ('<' '=')? number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT number number FALSE TRUE) |
	'|'  ('>' '=')? number '..' '<'        number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT number number TRUE FALSE) |
	'|'  '>'        number '..' '<'        number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT number number FALSE FALSE) |
	// relative to single value 
	'|' val=      number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT $val $val TRUE TRUE) |
	'|' ('>' '=') number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT number AST_NULL TRUE FALSE ) |
	'|' '>'       number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT number AST_NULL FALSE FALSE ) |
	'|' ('<' '=') number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT AST_NULL number FALSE TRUE ) |
	'|' '<'       number '|' -> ^(AST_NUMBER_INTERVAL_CONSTRAINT AST_NULL number FALSE FALSE ) 
	;
dateIntervalConstraint:
	// range
	'|'  ('>' '=')? ISO_DATE '..' ('<' '=')? ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT ISO_DATE ISO_DATE TRUE TRUE) |
	'|'  '>'        ISO_DATE '..' ('<' '=')? ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT ISO_DATE ISO_DATE FALSE TRUE) |
	'|'  ('>' '=')? ISO_DATE '..' '<'        ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT ISO_DATE ISO_DATE TRUE FALSE) |
	'|'  '>'        ISO_DATE '..' '<'        ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT ISO_DATE ISO_DATE FALSE FALSE) |
	// relative to single value 
	'|' val=      ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT $val $val TRUE TRUE) |
	'|' ('>' '=') ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT ISO_DATE AST_NULL TRUE FALSE ) |
	'|' '>'       ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT ISO_DATE AST_NULL FALSE FALSE ) |
	'|' ('<' '=') ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT AST_NULL ISO_DATE FALSE TRUE ) |
	'|' '<'       ISO_DATE '|' -> ^(AST_DATE_INTERVAL_CONSTRAINT AST_NULL ISO_DATE FALSE FALSE ) 
	;
timeIntervalConstraint:
	// range
	'|'  ('>' '=')? ISO_TIME '..' ('<' '=')? ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT ISO_TIME ISO_TIME TRUE TRUE) |
	'|'  '>'        ISO_TIME '..' ('<' '=')? ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT ISO_TIME ISO_TIME FALSE TRUE) |
	'|'  ('>' '=')? ISO_TIME '..' '<'        ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT ISO_TIME ISO_TIME TRUE FALSE) |
	'|'  '>'        ISO_TIME '..' '<'        ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT ISO_TIME ISO_TIME FALSE FALSE) |
	// relative to single value 
	'|' val=      ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT $val $val TRUE TRUE) |
	'|' ('>' '=') ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT ISO_TIME AST_NULL TRUE FALSE ) |
	'|' '>'       ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT ISO_TIME AST_NULL FALSE FALSE ) |
	'|' ('<' '=') ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT AST_NULL ISO_TIME FALSE TRUE ) |
	'|' '<'       ISO_TIME '|' -> ^(AST_TIME_INTERVAL_CONSTRAINT AST_NULL ISO_TIME FALSE FALSE ) 
	;
	
dateTimeIntervalConstraint:
	// range
	'|'  ('>' '=')? ISO_DATE_TIME '..' ('<' '=')? ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT ISO_DATE_TIME ISO_DATE_TIME TRUE TRUE) |
	'|'  '>'        ISO_DATE_TIME '..' ('<' '=')? ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT ISO_DATE_TIME ISO_DATE_TIME FALSE TRUE) |
	'|'  ('>' '=')? ISO_DATE_TIME '..' '<'        ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT ISO_DATE_TIME ISO_DATE_TIME TRUE FALSE) |
	'|'  '>'        ISO_DATE_TIME '..' '<'        ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT ISO_DATE_TIME ISO_DATE_TIME FALSE FALSE) |
	// relative to single value 
	'|' val=      ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT $val $val TRUE TRUE) |
	'|' ('>' '=') ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT ISO_DATE_TIME AST_NULL TRUE FALSE ) |
	'|' '>'       ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT ISO_DATE_TIME AST_NULL FALSE FALSE ) |
	'|' ('<' '=') ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT AST_NULL ISO_DATE_TIME FALSE TRUE ) |
	'|' '<'       ISO_DATE_TIME '|' -> ^(AST_DATE_TIME_INTERVAL_CONSTRAINT AST_NULL ISO_DATE_TIME FALSE FALSE ) 
	;
	
durationIntervalConstraint:
	// range
	'|'  ('>' '=')? DURATION '..' ('<' '=')? DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT DURATION DURATION TRUE TRUE) |
	'|'  '>'        DURATION '..' ('<' '=')? DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT DURATION DURATION FALSE TRUE) |
	'|'  ('>' '=')? DURATION '..' '<'        DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT DURATION DURATION TRUE FALSE) |
	'|'  '>'        DURATION '..' '<'        DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT DURATION DURATION FALSE FALSE) |
	// relative to single value 
	'|' val=        DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT $val $val TRUE TRUE) |
	'|' ('>' '=')   DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT DURATION AST_NULL TRUE FALSE ) |
	'|' '>'         DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT DURATION AST_NULL FALSE FALSE ) |
	'|' ('<' '=')   DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT AST_NULL DURATION FALSE TRUE ) |
	'|' '<'         DURATION '|' -> ^(AST_DURATION_INTERVAL_CONSTRAINT AST_NULL DURATION FALSE FALSE ) 
	;
	
		


// Ontology
ontology:	(ONTOLOGY|TERMINOLOGY) ^adlObjectValue;
annotations:	ANNOTATIONS ^adlObjectValue;

// Adl structure
adlValue 	
	: '<'!  ( adlMapValue | adlCodePhraseValueList |  openStringList | adlObjectValue | numberIntervalConstraint ) '>'! ';'!? 
	| '<' number '>' -> ^(AST_NUMBER_CONTAINER number)
	| '<' url '>' -> ^(AST_TEXT_CONTAINER url)
	| '<' '>' -> AST_NULL
	;

adlObjectValue:	
	( adlObjectProperty ) + -> ^(AST_ADL_OBJECT adlObjectProperty+);
adlObjectProperty:	
	identifier '=' adlValue -> ^(AST_ADL_OBJECT_PROPERTY identifier adlValue);

adlCodePhraseValueList:
	adlCodePhraseValue (',' adlCodePhraseValue)* -> ^(AST_CODE_PHRASE_LIST adlCodePhraseValue+);
adlCodePhraseValue
	: '[' tid=codeIdentifier '::' code=codeIdentifier ']' -> ^(AST_CODE_PHRASE $tid $code)
	;
adlMapValue:
	( adlMapValueEntry )+ -> ^(AST_ADL_MAP adlMapValueEntry+);
adlMapValueEntry:
	'[' STRING ']' '=' adlValue -> ^(AST_ADL_MAP_ENTRY STRING adlValue) ;

openStringList:
	STRING (',' STRING)* (',' '...')? -> ^(AST_STRING_LIST STRING+);
	
stringList:	STRING (',' STRING)* -> ^(AST_STRING_LIST STRING+);

numberList:	number (',' number)* -> ^(AST_NUMBER_LIST number+) ;
booleanList:	bool (',' bool)* -> ^(AST_BOOLEAN_LIST bool+);


codeIdentifier:
		codeIdentifierPart+ -> ^(AST_TEXT codeIdentifierPart+);
codeIdentifierPart:
		(identifier | number |ISO_DATE_TIME | ISO_DATE | '.'| '-' | '(' | ')') ;

identifier:
	nameIdentifier | typeIdentifier;
	
bool:
	(TRUE | FALSE)^;
nameIdentifier: 	
	NAME_IDENTIFIER | keyword | AT_CODE_VALUE;

typeIdentifierWithGenerics:
	typeIdentifier ('<' typeIdentifier '>')? -> ^(AST_TEXT typeIdentifier ('<' typeIdentifier '>')?);

typeIdentifier: 	
	TYPE_IDENTIFIER ;

keyword
	: LANGUAGE | ARCHETYPE | ONTOLOGY | DESCRIPTION | CONCEPT | SPECIALIZE 
	| DEFINITION | MATCHES | USE_NODE | OCCURRENCES | CARDINALITY | ORDERED
	| UNORDERED | EXISTENCE | CLOSED | ANNOTATIONS | TEMPLATE | TEMPLATE_OVERLAY
	| SPECIALISE
	;

		
pathSegment: nameIdentifier atCode? ;
rmPath: '/'? pathSegment ('/' pathSegment)* -> ^(AST_TEXT);


url:
	identifier ':' (codeIdentifierPart|'_'|'.'|'='|'?'|'-'|'/'|'&'|';'| UNICODE_CHAR)+ -> AST_TEXT;

number:
	'-'? INTEGER ('.' INTEGER)? -> AST_NUMBER;

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

ISO_DATE_TIME
	  : DIGIT DIGIT DIGIT DIGIT 
		( ('-' DIGIT DIGIT)=>	'-' DIGIT DIGIT ('-' DIGIT DIGIT)? 
			( ('T' DIGIT DIGIT)=> 	'T' ISO_TIME_PART
			| 			{$type=ISO_DATE;}
			)
		| 			{$type=INTEGER;}
		);
		

// set from inside ISO_DATE_TIME
fragment ISO_DATE: .;

ISO_TIME:			DIGIT DIGIT 
					( (':' DIGIT DIGIT)=> 	':' DIGIT DIGIT (':' DIGIT DIGIT (',' DIGIT (DIGIT (DIGIT DIGIT?)?)? )? )? ISO_TIMEZONE? 
					|  			{$type=INTEGER;}
					);

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

	

LINE_COMMENT: 	'--' (options {greedy=false;} : .)* '\n' {$channel=HIDDEN;} ;
WS:   		( ' ' | '\t' | '\r' | '\n') {$channel=HIDDEN;};

