package com.repetentia.commons.web.component;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

public class SqlStringHelper {

    private static final int ALIAS_TRUNCATE_LENGTH = 10;
    public static final String WHITESPACE = " \n\r\f\t";
    public static final String[] EMPTY_STRINGS = new String[0];

    private SqlStringHelper() {
    }

    public static int lastIndexOfLetter(String string) {
        for ( int i = 0; i < string.length(); i++ ) {
            char character = string.charAt( i );
            if ( !Character.isLetter( character ) && !( '_' == character ) ) {
                return i - 1;
            }
        }
        return string.length() - 1;
    }

    public static String joinWithQualifierAndSuffix(
            String[] values,
            String qualifier,
            String suffix,
            String deliminator) {
        int length = values.length;
        if ( length == 0 ) {
            return "";
        }
        StringBuilder buf = new StringBuilder( length * ( values[0].length() + suffix.length() ) )
                .append( qualify( qualifier, values[0] ) ).append( suffix );
        for ( int i = 1; i < length; i++ ) {
            buf.append( deliminator ).append( qualify( qualifier, values[i] ) ).append( suffix );
        }
        return buf.toString();
    }

    public static String join(String separator, Iterator<?> objects) {
        StringBuilder buf = new StringBuilder();
        if ( objects.hasNext() ) {
            buf.append( objects.next() );
        }
        while ( objects.hasNext() ) {
            buf.append( separator ).append( objects.next() );
        }
        return buf.toString();
    }

    public static String[] add(String[] x, String sep, String[] y) {
        final String[] result = new String[x.length];
        for ( int i = 0; i < x.length; i++ ) {
            result[i] = x[i] + sep + y[i];
        }
        return result;
    }

    public static String repeat(String string, int times) {
        StringBuilder buf = new StringBuilder( string.length() * times );
        for ( int i = 0; i < times; i++ ) {
            buf.append( string );
        }
        return buf.toString();
    }

    public static String repeat(String string, int times, String deliminator) {
        StringBuilder buf = new StringBuilder( ( string.length() * times ) + ( deliminator.length() * ( times - 1 ) ) )
                .append( string );
        for ( int i = 1; i < times; i++ ) {
            buf.append( deliminator ).append( string );
        }
        return buf.toString();
    }

    public static String repeat(char character, int times) {
        char[] buffer = new char[times];
        Arrays.fill( buffer, character );
        return new String( buffer );
    }

    public static String replace(String template, String placeholder, String replacement) {
        return replace( template, placeholder, replacement, false );
    }

    public static String[] replace(String[] templates, String placeholder, String replacement) {
        String[] result = new String[templates.length];
        for ( int i = 0; i < templates.length; i++ ) {
            result[i] = replace( templates[i], placeholder, replacement );
        }
        return result;
    }

    public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
        return replace( template, placeholder, replacement, wholeWords, false );
    }

    public static String replace(
            String template,
            String placeholder,
            String replacement,
            boolean wholeWords,
            boolean encloseInParensIfNecessary) {
        if ( template == null ) {
            return null;
        }
        int loc = indexOfPlaceHolder( template, placeholder, wholeWords );
        if ( loc < 0 ) {
            return template;
        }
        else {
            String beforePlaceholder = template.substring( 0, loc );
            String afterPlaceholder = template.substring( loc + placeholder.length() );
            return replace(
                    beforePlaceholder,
                    afterPlaceholder,
                    placeholder,
                    replacement,
                    wholeWords,
                    encloseInParensIfNecessary
            );
        }
    }

    public static String replace(
            String beforePlaceholder,
            String afterPlaceholder,
            String placeholder,
            String replacement,
            boolean wholeWords,
            boolean encloseInParensIfNecessary) {
        final boolean actuallyReplace =
                !wholeWords
                        || afterPlaceholder.length() == 0
                        || !Character.isJavaIdentifierPart( afterPlaceholder.charAt( 0 ) );
        boolean encloseInParens =
                actuallyReplace
                        && encloseInParensIfNecessary
                        && !( getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' ) &&
                        !( getLastNonWhitespaceCharacter( beforePlaceholder ) == ',' && getFirstNonWhitespaceCharacter(
                                afterPlaceholder ) == ')' );
        StringBuilder buf = new StringBuilder( beforePlaceholder );
        if ( encloseInParens ) {
            buf.append( '(' );
        }
        buf.append( actuallyReplace ? replacement : placeholder );
        if ( encloseInParens ) {
            buf.append( ')' );
        }
        buf.append(
                replace(
                        afterPlaceholder,
                        placeholder,
                        replacement,
                        wholeWords,
                        encloseInParensIfNecessary
                )
        );
        return buf.toString();
    }

    private static int indexOfPlaceHolder(String template, String placeholder, boolean wholeWords) {
        if ( wholeWords ) {
            int placeholderIndex = -1;
            boolean isPartialPlaceholderMatch;
            do {
                placeholderIndex = template.indexOf( placeholder, placeholderIndex + 1 );
                isPartialPlaceholderMatch = placeholderIndex != -1 &&
                        template.length() > placeholderIndex + placeholder.length() &&
                        Character.isJavaIdentifierPart( template.charAt( placeholderIndex + placeholder.length() ) );
            } while ( placeholderIndex != -1 && isPartialPlaceholderMatch );

            return placeholderIndex;
        }
        else {
            return template.indexOf( placeholder );
        }
    }

    public static int indexOfIdentifierWord(String str, String word) {
        if ( str == null || str.length() == 0 || word == null || word.length() == 0 ) {
            return -1;
        }

        int position = str.indexOf( word );
        while ( position >= 0 && position < str.length() ) {
            if (
                    ( position == 0 || !Character.isJavaIdentifierPart( str.charAt( position - 1 ) ) ) &&
                    ( position + word.length() == str.length() || !Character.isJavaIdentifierPart( str.charAt( position + word.length() ) ) )
            ) {
                return position;
            }
            position = str.indexOf( word, position + 1 );
        }

        return -1;
    }

    public static char getLastNonWhitespaceCharacter(String str) {
        if ( str != null && str.length() > 0 ) {
            for ( int i = str.length() - 1; i >= 0; i-- ) {
                char ch = str.charAt( i );
                if ( !Character.isWhitespace( ch ) ) {
                    return ch;
                }
            }
        }
        return '\0';
    }

    public static char getFirstNonWhitespaceCharacter(String str) {
        if ( str != null && str.length() > 0 ) {
            for ( int i = 0; i < str.length(); i++ ) {
                char ch = str.charAt( i );
                if ( !Character.isWhitespace( ch ) ) {
                    return ch;
                }
            }
        }
        return '\0';
    }

    public static String replaceOnce(String template, String placeholder, String replacement) {
        if ( template == null ) {
            return null;  // returning null!
        }
        int loc = template.indexOf( placeholder );
        if ( loc < 0 ) {
            return template;
        }
        else {
            return template.substring( 0, loc ) + replacement + template.substring( loc + placeholder.length() );
        }
    }


    public static String[] split(String separators, String list) {
        return split( separators, list, false );
    }

    public static String[] split(String separators, String list, boolean include) {
        StringTokenizer tokens = new StringTokenizer( list, separators, include );
        String[] result = new String[tokens.countTokens()];
        int i = 0;
        while ( tokens.hasMoreTokens() ) {
            result[i++] = tokens.nextToken();
        }
        return result;
    }

    public static String[] splitTrimmingTokens(String separators, String list, boolean include) {
        StringTokenizer tokens = new StringTokenizer( list, separators, include );
        String[] result = new String[tokens.countTokens()];
        int i = 0;
        while ( tokens.hasMoreTokens() ) {
            result[i++] = tokens.nextToken().trim();
        }
        return result;
    }

    public static String unqualify(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf( '.' );
        return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1 );
    }

    public static String qualifier(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf( '.' );
        return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
    }

    public static String collapse(String name) {
        if ( name == null ) {
            return null;
        }
        int breakPoint = name.lastIndexOf( '.' );
        if ( breakPoint < 0 ) {
            return name;
        }
        return collapseQualifier(
                name.substring( 0, breakPoint ),
                true
        ) + name.substring( breakPoint ); // includes last '.'
    }

    public static String collapseQualifier(String qualifier, boolean includeDots) {
        StringTokenizer tokenizer = new StringTokenizer( qualifier, "." );
        StringBuilder sb = new StringBuilder();
        sb.append( Character.toString( tokenizer.nextToken().charAt( 0 ) ) );
        while ( tokenizer.hasMoreTokens() ) {
            if ( includeDots ) {
                sb.append( '.' );
            }
            sb.append( tokenizer.nextToken().charAt( 0 ) );
        }
        return sb.toString();
    }

    public static String partiallyUnqualify(String name, String qualifierBase) {
        if ( name == null || !name.startsWith( qualifierBase ) ) {
            return name;
        }
        return name.substring( qualifierBase.length() + 1 ); // +1 to start after the following '.'
    }

    public static String collapseQualifierBase(String name, String qualifierBase) {
        if ( name == null || !name.startsWith( qualifierBase ) ) {
            return collapse( name );
        }
        return collapseQualifier( qualifierBase, true ) + name.substring( qualifierBase.length() );
    }

    public static String[] suffix(String[] columns, String suffix) {
        if ( suffix == null ) {
            return columns;
        }
        String[] qualified = new String[columns.length];
        for ( int i = 0; i < columns.length; i++ ) {
            qualified[i] = suffix( columns[i], suffix );
        }
        return qualified;
    }

    private static String suffix(String name, String suffix) {
        return ( suffix == null ) ? name : name + suffix;
    }

    public static String root(String qualifiedName) {
        int loc = qualifiedName.indexOf( '.' );
        return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( 0, loc );
    }

    public static String unroot(String qualifiedName) {
        int loc = qualifiedName.indexOf( '.' );
        return ( loc < 0 ) ? qualifiedName : qualifiedName.substring( loc + 1, qualifiedName.length() );
    }

    public static String toString(Object[] array) {
        int len = array.length;
        if ( len == 0 ) {
            return "";
        }
        StringBuilder buf = new StringBuilder( len * 12 );
        for ( int i = 0; i < len - 1; i++ ) {
            buf.append( array[i] ).append( ", " );
        }
        return buf.append( array[len - 1] ).toString();
    }

    public static String[] multiply(String string, Iterator<String> placeholders, Iterator<String[]> replacements) {
        String[] result = new String[] {string};
        while ( placeholders.hasNext() ) {
            result = multiply( result, placeholders.next(), replacements.next() );
        }
        return result;
    }

    private static String[] multiply(String[] strings, String placeholder, String[] replacements) {
        String[] results = new String[replacements.length * strings.length];
        int n = 0;
        for ( String replacement : replacements ) {
            for ( String string : strings ) {
                results[n++] = replaceOnce( string, placeholder, replacement );
            }
        }
        return results;
    }

    public static int countUnquoted(String string, char character) {
        if ( '\'' == character ) {
            throw new IllegalArgumentException( "Unquoted count of quotes is invalid" );
        }
        if ( string == null ) {
            return 0;
        }
        int count = 0;
        int stringLength = string.length();
        boolean inQuote = false;
        for ( int indx = 0; indx < stringLength; indx++ ) {
            char c = string.charAt( indx );
            if ( inQuote ) {
                if ( '\'' == c ) {
                    inQuote = false;
                }
            }
            else if ( '\'' == c ) {
                inQuote = true;
            }
            else if ( c == character ) {
                count++;
            }
        }
        return count;
    }

    public static boolean isNotEmpty(String string) {
        return string != null && string.length() > 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isEmptyOrWhiteSpace(String string) {
        return isEmpty( string ) || isEmpty( string.trim() );
    }

    public static String qualify(String prefix, String name) {
        if ( name == null || prefix == null ) {
            throw new NullPointerException( "prefix or name were null attempting to build qualified name" );
        }
        return prefix + '.' + name;
    }

    public static String qualifyConditionally(String prefix, String name) {
        if ( name == null ) {
            throw new NullPointerException( "name was null attempting to build qualified name" );
        }
        return isEmpty( prefix ) ? name : prefix + '.' + name;
    }

    public static String[] qualify(String prefix, String[] names) {
        if ( prefix == null ) {
            return names;
        }
        int len = names.length;
        String[] qualified = new String[len];
        for ( int i = 0; i < len; i++ ) {
            qualified[i] = qualify( prefix, names[i] );
        }
        return qualified;
    }

    public static int firstIndexOfChar(String sqlString, BitSet keys, int startindex) {
        for ( int i = startindex, size = sqlString.length(); i < size; i++ ) {
            if ( keys.get( sqlString.charAt( i ) ) ) {
                return i;
            }
        }
        return -1;
    }

    public static int firstIndexOfChar(String sqlString, String string, int startindex) {
        BitSet keys = new BitSet();
        for ( int i = 0, size = string.length(); i < size; i++ ) {
            keys.set( string.charAt( i ) );
        }
        return firstIndexOfChar( sqlString, keys, startindex );
    }

    public static String truncate(String string, int length) {
        if ( string.length() <= length ) {
            return string;
        }
        else {
            return string.substring( 0, length );
        }
    }

    public static String generateAlias(String description) {
        return generateAliasRoot( description ) + '_';
    }

    private static String generateAliasRoot(String description) {
        String result = truncate( unqualifyEntityName( description ), ALIAS_TRUNCATE_LENGTH )
                .toLowerCase( Locale.ROOT )
                .replace( '/', '_' ) // entityNames may now include slashes for the representations
                .replace( '$', '_' ); //classname may be an inner class
        result = cleanAlias( result );
        if ( Character.isDigit( result.charAt( result.length() - 1 ) ) ) {
            return result + "x"; //ick!
        }
        else {
            return result;
        }
    }

    private static String cleanAlias(String alias) {
        char[] chars = alias.toCharArray();
        // short cut check...
        if ( !Character.isLetter( chars[0] ) ) {
            for ( int i = 1; i < chars.length; i++ ) {
                // as soon as we encounter our first letter, return the substring
                // from that position
                if ( Character.isLetter( chars[i] ) ) {
                    return alias.substring( i );
                }
            }
        }
        return alias;
    }

    public static String unqualifyEntityName(String entityName) {
        String result = unqualify( entityName );
        int slashPos = result.indexOf( '/' );
        if ( slashPos > 0 ) {
            result = result.substring( 0, slashPos - 1 );
        }
        return result;
    }

    public static String moveAndToBeginning(String filter) {
        if ( filter.trim().length() > 0 ) {
            filter += " and ";
            if ( filter.startsWith( " and " ) ) {
                filter = filter.substring( 4 );
            }
        }
        return filter;
    }

    public static boolean isQuoted(final String name) {
        if ( name == null || name.isEmpty() ) {
            return false;
        }

        final char first = name.charAt( 0 );
        final char last = name.charAt( name.length() - 1 );

        return ( ( first == last ) && ( first == '`' || first == '"' ) );
    }

    public static String unquote(String name) {
        return isQuoted( name ) ? name.substring( 1, name.length() - 1 ) : name;
    }

    public static final String BATCH_ID_PLACEHOLDER = "$$BATCH_ID_PLACEHOLDER$$";


    public static String nullIfEmpty(String value) {
        return isEmpty( value ) ? null : value;
    }

    public static <T> String join(Collection<T> values, Renderer<T> renderer) {
        final StringBuilder buffer = new StringBuilder();
        for ( T value : values ) {
            buffer.append( String.join(", ", renderer.render( value ) ) );
        }
        return buffer.toString();
    }

    public interface Renderer<T> {
        String render(T value);
    }

    /**
     * Return the interned form of a String, or null if the parameter is null.
     * <p>
     * Use with caution: excessive interning is known to cause issues.
     * Best to use only with strings which are known to be long lived constants,
     * and for which the chances of being actual duplicates is proven.
     * (Even better: avoid needing interning by design changes such as reusing
     * the known reference)
     * @param string The string to intern.
     * @return The interned string.
     */
    public static String safeInterning(final String string) {
        if ( string == null ) {
            return null;
        }
        else {
            return string.intern();
        }
    }

}
