Java ADL 1.5 Parser
------------------

DESCRIPTION
        A java implementation of an ADL 1.5 parser and flattener. It can parse .adls files
        and flatten the resulting differential archetypes into flat archetypes.
        It also provides some validation of archetypes after parsing.

STATUS
        Based on Archetype Definition Language (ADL) 1.5 and Archetype Object Model and other
        related Information Models from revision 1.5.4 (July 2014)

        Currently it's also backward compatible with ADL 1.4.


USAGE:

Parse source archetype:
    AdlDeserializer deserializer = new AdlDeserializer(new OpenEhrRmModel();
    String adl = ...; // read a .adls file into a string
    DifferentialArchetype archetype = deserializer.parse(adl);




Flatten differential archetype:
    ArchetypeFlattener flattener = new ArchetypeFlattener(new OpenEhrRmModel());

    DifferentialArchetype specializedArchetype = ...; // parse source archetype
    FlatArchetype flatParent = ...; // get flattened parent archetype, or null if no parent

    FlatArchetype flatArchetype = flattener.flatten(flatParent, specializedArchetype);




Validate flat archetype:
    FlatArchetype flatArchetype = ...; // parse and flatten archetype
    ArchetypeValidator validator = new ArchetypeValidator(new OpenEhrRmModel(), flatArchetype);
    validator.validate();
    List<AqlValidationError> errors = validator.getErrors();




