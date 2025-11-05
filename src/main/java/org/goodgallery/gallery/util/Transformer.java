package org.goodgallery.gallery.util;

public interface Transformer<I, O> {

  O transform(I input) throws Exception;

}