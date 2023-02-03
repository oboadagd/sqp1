package com.lucidworks.fusion.semanticsearch.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.StringEscapeUtils.escapeXml;

@Getter
@Setter
@NoArgsConstructor
public class SemanticEdge extends DefaultEdge {
    public static final String TOKEN_ATTRNAME = "token";
    public static final String MODIFIERS_ATTRNAME = "modifiers";
    public static final String FIELDS_ATTRNAME = "fields";
    public static final String LABEL_ATTRNAME = "label";
    public static final String COLOR_ATTRNAME = "color";
    public static final String DELIMITER = ",";
    public static final String QUALIFIER = "=";
    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    private String token;
    private final List<SemanticModifier> modifiers = new LinkedList<>();
    private final Set<Field> fields = new LinkedHashSet<>();
    private final List<String> comprehensionIds = new ArrayList<>();

    public SemanticEdge(String token) {
        this.token = token;
    }

    public SemanticEdge(
            final String token,
            final Iterable<SemanticEdge> parents,
            boolean inheritModifiers,
            boolean inheritFields) {
        this(token);
        for (SemanticEdge parent : parents) {
            if (inheritModifiers) {
                this.modifiers.addAll(parent.modifiers);
            }
            if (inheritFields) {
                this.fields.addAll(parent.fields);
            }
        }
    }

    public static void consumeAttribute(Pair<SemanticEdge, String> pair, Attribute attr) {
        switch (pair.getSecond()) {
            case TOKEN_ATTRNAME:
                pair.getFirst().setToken(attr.getValue());
                break;
            case MODIFIERS_ATTRNAME:
                List<SemanticModifier> parsedNorms =
                        Arrays.stream(attr.getValue().split(DELIMITER))
                                .map(SemanticModifier::valueOf)
                                .collect(Collectors.toList());
                pair.getFirst().getModifiers().addAll(parsedNorms);
                break;
            case FIELDS_ATTRNAME:
                Set<Field> parsedFields =
                        Arrays.stream(attr.getValue().split(DELIMITER))
                                .map(SemanticEdge::consumeField)
                                .collect(toSet());
                pair.getFirst().getFields().addAll(parsedFields);
        }
    }

    private static Field consumeField(String fieldString) {
        String[] arr = fieldString.split(QUALIFIER);
        Field field = Field.builder()
                .name(arr[0])
                .type(FieldMatchType.valueOf(arr[1]))
                .build();

        if (arr.length > 2) {
            field.setBoost(Float.parseFloat(arr[2]));
        }

        return field;
    }

    public static Map<String, Attribute> provideAttribute(SemanticEdge edge) {
        Map<String, Attribute> map = new LinkedHashMap<>();
        if (edge.getToken() != null) {
            map.put(TOKEN_ATTRNAME, DefaultAttribute.createAttribute(edge.getToken()));
        }
        if (!edge.getModifiers().isEmpty()) {
            String combinedNorms = edge.getModifiers().stream()
                    .map(SemanticModifier::name)
                    .collect(Collectors.joining(DELIMITER));
            map.put(MODIFIERS_ATTRNAME, DefaultAttribute.createAttribute(combinedNorms));
            if (edge.containsComprehension()) {
                map.put(COLOR_ATTRNAME, DefaultAttribute.createAttribute("red"));
            }
        } else {
            map.put(COLOR_ATTRNAME, DefaultAttribute.createAttribute("blue"));
        }
        if (!edge.getFields().isEmpty()) {
            String combinedFields = edge.getFields().stream()
                    .map(SemanticEdge::provideField)
                    .collect(Collectors.joining(DELIMITER));
            map.put(FIELDS_ATTRNAME, DefaultAttribute.createAttribute(combinedFields));
        }
        map.put(LABEL_ATTRNAME,
                new DefaultAttribute<>(edge.toString(), AttributeType.HTML));
        return map;
    }

    private static String provideField(Field field) {
        List<String> fields = new ArrayList<>();
        fields.add(field.getName());
        fields.add(field.getType().name());
        if (field.getBoost() != null) {
            fields.add(field.getBoost().toString());
        }
        return String.join(QUALIFIER, fields);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<b>'").append(escapeXml(token));
        if (!modifiers.isEmpty()) {
            builder.append(" (")
                    .append(modifiers.stream()
                            .map(SemanticModifier::getShortName)
                            .collect(Collectors.joining(DELIMITER)))
                    .append(")");
        }
        builder.append("'</b>");

        for (Field field : fields) {
            builder.append("<br />")
                    .append(field.getName())
                    .append(" (")
                    .append(field.getType().getShortName());

            if (field.getBoost() != null) {
                builder.append(", ").append(FORMAT.format(field.getBoost()));
            }

            builder.append(")");
        }
        return builder.toString();
    }

    public boolean contains(FieldMatchType target) {
        return fields.stream().anyMatch(field -> field.getType() == target);
    }

    public boolean containsComprehension() {
        return modifiers.stream().anyMatch(SemanticModifier::isComprehension);
    }

    public boolean contains(SemanticModifier target) {
        return modifiers.contains(target);
    }

    public long getWordCount() {
        return Integer.parseInt(getTarget()) - Integer.parseInt(getSource());
    }

    public SemanticEdge createCopy(String token) {
        return new SemanticEdge(token, List.of(this), true, true);
    }

    public String getSource() {
        return (String) super.getSource();
    }

    public String getTarget() {
        return (String) super.getTarget();
    }

    public boolean isEqualTo(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticEdge edge = (SemanticEdge) o;
        return Objects.equals(token, edge.token) && modifiers.equals(edge.modifiers) && fields.equals(edge.fields)
                && comprehensionIds.equals(edge.comprehensionIds);
    }
}