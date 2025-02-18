package evaluator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;

import java.util.Set;

public class Rule {
  private static final Set<String> VALID_NAMES;
  private static final Set<String> VALID_FORMATS;

  static {
    VALID_NAMES = ImmutableSet.of("title", "description", "price", "low_price",
        "currency", "availability", "brand", "image_urls", "color", "size", "material",
        "fit", "gender", "category");
    VALID_FORMATS = ImmutableSet.of("TEXT", "LIST_TEXT");
  }

  @JsonProperty
  String name;

  @JsonProperty
  String xPath;

  XPathSelector evaluator;

  @JsonProperty("output_format")
  String outputFormat;

  public void validateAndInitialize(XPathCompiler xPathCompiler) {
    Preconditions.checkArgument(
        !Strings.isNullOrEmpty(name),
        "Name must be non-empty!");

    Preconditions.checkArgument(
        !Strings.isNullOrEmpty(xPath),
        "xPath must be non-empty!");

    Preconditions.checkArgument(
        !Strings.isNullOrEmpty(outputFormat),
        "output_format must be non-empty!");
    Preconditions.checkArgument(VALID_NAMES.contains(name.toLowerCase()),
        "Name must be one of {" + String.join(", ", VALID_NAMES) + "}");
    Preconditions.checkArgument(VALID_FORMATS.contains(outputFormat.toUpperCase()),
        "output_format must be one of {" + String.join(", ", VALID_FORMATS) + "}");
    try {
      evaluator = xPathCompiler.compile(xPath).load();
    } catch (SaxonApiException e) {
      throw new RuntimeException("Failed to compile xPath: " + xPath, e);
    }
  }
}
