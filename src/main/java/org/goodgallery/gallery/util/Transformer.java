package org.goodgallery.gallery.util;

public interface Transformer<I, O> {

  /**
 * Transforms the given input into an output.
 *
 * @param input the value to transform
 * @return the transformed output
 * @throws Throwable if the transformation cannot be completed
 */
O transform(I input) throws Throwable;

}