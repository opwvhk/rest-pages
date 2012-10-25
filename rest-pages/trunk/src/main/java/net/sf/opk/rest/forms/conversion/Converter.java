package net.sf.opk.rest.forms.conversion;

import java.util.List;
import javax.ws.rs.QueryParam;

import com.fasterxml.classmate.ResolvedType;


/**
 * A converter that supports generics. All standard implementations together convert {@code List}s of {@code String}s to
 * arrays and collections as defined for {@link QueryParam}.
 *
 * @author <a href="mailto:oscar@westravanholthe.nl">Oscar Westra van Holthe - Kind</a>
 */
public interface Converter
{
	boolean canConvertTo(ResolvedType resolvedType);

	<T> T convertTo(ResolvedType resolvedType, List<String> values);
}
