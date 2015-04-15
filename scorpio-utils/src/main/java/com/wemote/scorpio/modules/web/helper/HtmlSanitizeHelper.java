package com.wemote.scorpio.modules.web.helper;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import org.owasp.html.Handler;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;
import org.owasp.html.PolicyFactory;
import org.owasp.html.TagBalancingHtmlStreamEventReceiver;

import java.util.regex.Pattern;

/**
 * @author : jayon.xu@gmail.com
 */
public class HtmlSanitizeHelper {

    // Some common regular expression definitions.

    // The 16 colors defined by the HTML Spec (also used by the CSS Spec)
    private static final Pattern COLOR_NAME = Pattern.compile(
            "(?:aqua|black|blue|fuchsia|gray|grey|green|lime|maroon|navy|olive|purple"
                    + "|red|silver|teal|white|yellow)");

    // HTML/CSS Spec allows 3 or 6 digit hex to specify color
    private static final Pattern COLOR_CODE = Pattern.compile(
            "(?:#(?:[0-9a-fA-F]{3}(?:[0-9a-fA-F]{3})?))");

    private static final Pattern NUMBER_OR_PERCENT = Pattern.compile(
            "[0-9]+%?");
    private static final Pattern PARAGRAPH = Pattern.compile(
            "(?:[\\p{L}\\p{N},'\\.\\s\\-_\\(\\)]|&[0-9]{2};)*");
    private static final Pattern HTML_ID = Pattern.compile(
            "[a-zA-Z0-9:\\-_\\.]+");
    // force non-empty with a '+' at the end instead of '*'
    private static final Pattern HTML_TITLE = Pattern.compile(
            "[\\p{L}\\p{N}\\s\\-_',:\\[\\]!\\./\\\\\\(\\)&]*");
    private static final Pattern HTML_CLASS = Pattern.compile(
            "[a-zA-Z0-9\\s,\\-_]+");

    private static final Pattern ONSITE_URL = Pattern.compile(
            "(?:[\\p{L}\\p{N}\\\\\\.#@\\$%\\+&;\\-_~,\\?=/!]+|#(\\w)+)");
    private static final Pattern OFFSITE_URL = Pattern.compile(
            "\\s*(?:(?:ht|f)tps?://|mailto:)[\\p{L}\\p{N}]"
                    + "[\\p{L}\\p{N}\\p{Zs}\\.#@\\$%\\+&;:\\-_~,\\?=/!\\(\\)]*+\\s*");

    private static final Pattern NUMBER = Pattern.compile(
            "[+-]?(?:(?:[0-9]+(?:\\.[0-9]*)?)|\\.[0-9]+)");

    private static final Pattern NAME = Pattern.compile("[a-zA-Z0-9\\-_\\$]+");

    private static final Pattern ALIGN = Pattern.compile(
            "(?i)center|left|right|justify|char");

    private static final Pattern VALIGN = Pattern.compile(
            "(?i)baseline|bottom|middle|top");

    private static final Predicate<String> COLOR_NAME_OR_COLOR_CODE
            = new Predicate<String>() {
        public boolean apply(String s) {
            return COLOR_NAME.matcher(s).matches()
                    || COLOR_CODE.matcher(s).matches();
        }
    };

    private static final Predicate<String> ONSITE_OR_OFFSITE_URL
            = new Predicate<String>() {
        public boolean apply(String s) {
            return ONSITE_URL.matcher(s).matches()
                    || OFFSITE_URL.matcher(s).matches();
        }
    };

    private static final Pattern HISTORY_BACK = Pattern.compile(
            "(?:javascript:)?\\Qhistory.go(-1)\\E");

    private static final Pattern ONE_CHAR = Pattern.compile(
            ".?", Pattern.DOTALL);

    public static final PolicyFactory POLICY_DEFINITION = new HtmlPolicyBuilder()
            .allowAttributes("id").matching(HTML_ID).globally()
            .allowAttributes("class").matching(HTML_CLASS).globally()
            .allowAttributes("lang").matching(Pattern.compile("[a-zA-Z]{2,20}"))
            .globally()
            .allowAttributes("title").matching(HTML_TITLE).globally()
            .allowStyling()
            .allowAttributes("align").matching(ALIGN).onElements("p")
            .allowAttributes("for").matching(HTML_ID).onElements("label")
            .allowAttributes("color").matching(COLOR_NAME_OR_COLOR_CODE)
            .onElements("font")
            .allowAttributes("face")
            .matching(Pattern.compile("[\\w;, \\-]+"))
            .onElements("font")
            .allowAttributes("size").matching(NUMBER).onElements("font")
            .allowAttributes("href").matching(ONSITE_OR_OFFSITE_URL)
            .onElements("a")
            .allowStandardUrlProtocols()
            .allowAttributes("nohref").onElements("a")
            .allowAttributes("name").matching(NAME).onElements("a")
            .allowAttributes(
                    "onfocus", "onblur", "onclick", "onmousedown", "onmouseup")
            .matching(HISTORY_BACK).onElements("a")
            .requireRelNofollowOnLinks()
            .allowAttributes("src").matching(ONSITE_OR_OFFSITE_URL)
            .onElements("img")
            .allowAttributes("name").matching(NAME)
            .onElements("img")
            .allowAttributes("alt").matching(PARAGRAPH)
            .onElements("img")
            .allowAttributes("border", "hspace", "vspace").matching(NUMBER)
            .onElements("img")
            .allowAttributes("border", "cellpadding", "cellspacing")
            .matching(NUMBER).onElements("table")
            .allowAttributes("bgcolor").matching(COLOR_NAME_OR_COLOR_CODE)
            .onElements("table")
            .allowAttributes("background").matching(ONSITE_URL)
            .onElements("table")
            .allowAttributes("align").matching(ALIGN)
            .onElements("table")
            .allowAttributes("noresize").matching(Pattern.compile("(?i)noresize"))
            .onElements("table")
            .allowAttributes("background").matching(ONSITE_URL)
            .onElements("td", "th", "tr")
            .allowAttributes("bgcolor").matching(COLOR_NAME_OR_COLOR_CODE)
            .onElements("td", "th")
            .allowAttributes("abbr").matching(PARAGRAPH)
            .onElements("td", "th")
            .allowAttributes("axis", "headers").matching(NAME)
            .onElements("td", "th")
            .allowAttributes("scope")
            .matching(Pattern.compile("(?i)(?:row|col)(?:group)?"))
            .onElements("td", "th")
            .allowAttributes("nowrap")
            .onElements("td", "th")
            .allowAttributes("height", "width").matching(NUMBER_OR_PERCENT)
            .onElements("table", "td", "th", "tr", "img")
            .allowAttributes("align").matching(ALIGN)
            .onElements("thead", "tbody", "tfoot", "img",
                    "td", "th", "tr", "colgroup", "col")
            .allowAttributes("valign").matching(VALIGN)
            .onElements("thead", "tbody", "tfoot",
                    "td", "th", "tr", "colgroup", "col")
            .allowAttributes("charoff").matching(NUMBER_OR_PERCENT)
            .onElements("td", "th", "tr", "colgroup", "col",
                    "thead", "tbody", "tfoot")
            .allowAttributes("char").matching(ONE_CHAR)
            .onElements("td", "th", "tr", "colgroup", "col",
                    "thead", "tbody", "tfoot")
            .allowAttributes("colspan", "rowspan").matching(NUMBER)
            .onElements("td", "th")
            .allowAttributes("span", "width").matching(NUMBER_OR_PERCENT)
            .onElements("colgroup", "col")
            .allowElements(
                    "a", "blockquote", "dd", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5",
                    "h6", "i", "img", "li", "ol", "p", "span", "sup", "sub", "strong", "table",
                    "tbody", "td", "th", "thead", "tr", "ul", "br", "hr")
            .toFactory();

    public static String sanitize(String html) {
        if (Strings.isNullOrEmpty(html)) {
            return "";
        }

        StringBuilder htmlOut = new StringBuilder();
        HtmlSanitizer.Policy policy = POLICY_DEFINITION.apply(
                // The tag balancer passes events to AppendDomainAfterText which
                // assumes that openTag and closeTag events line up with one-another.
                new TagBalancingHtmlStreamEventReceiver(
                        HtmlStreamRenderer.create(htmlOut, Handler.DO_NOTHING)
                )
        );
        assert policy != null;
        HtmlSanitizer.sanitize(html, policy);

        return htmlOut.toString();
    }

    public static String sanitizeTotally(String html) {
        if (Strings.isNullOrEmpty(html)) {
            return "";
        }

        StringBuilder htmlOut = new StringBuilder();
        HtmlSanitizer.Policy policy = new HtmlPolicyBuilder().allowElements().toFactory().apply(
                // The tag balancer passes events to AppendDomainAfterText which
                // assumes that openTag and closeTag events line up with one-another.
                new TagBalancingHtmlStreamEventReceiver(
                        HtmlStreamRenderer.create(htmlOut, Handler.DO_NOTHING)
                )
        );
        assert policy != null;
        HtmlSanitizer.sanitize(html, policy);

        return htmlOut.toString();
    }


}
