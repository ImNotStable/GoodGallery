package org.goodgallery.gallery.util;

public interface Transformer<I, O> {

  /**
 * Transforms the given input into a result.
 *
 * @param input the value to transform
 * @return the transformed result
 * @throws Throwable if the transformation cannot be completed
 */
O transform(I input) throws Throwable;

}