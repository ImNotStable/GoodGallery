package org.goodgallery.gallery.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransformerTest {

  @Test
  void testTransformer_withSimpleTransformation() throws Throwable {
    Transformer<String, Integer> transformer = String::length;
    
    Integer result = transformer.transform("Hello");
    assertEquals(5, result);
  }

  @Test
  void testTransformer_withComplexTransformation() throws Throwable {
    Transformer<String, String> transformer = input -> input.toUpperCase() + "!";
    
    String result = transformer.transform("hello");
    assertEquals("HELLO!", result);
  }

  @Test
  void testTransformer_canThrowException() {
    Transformer<String, Integer> transformer = input -> {
      if (input == null) throw new IllegalArgumentException("Input cannot be null");
      return input.length();
    };
    
    assertThrows(IllegalArgumentException.class, () -> transformer.transform(null));
  }

  @Test
  void testTransformer_withNullInput() throws Throwable {
    Transformer<String, String> transformer = input -> input == null ? "NULL" : input;
    
    String result = transformer.transform(null);
    assertEquals("NULL", result);
  }

  @Test
  void testTransformer_chainingMultiple() throws Throwable {
    Transformer<String, Integer> lengthTransformer = String::length;
    Transformer<Integer, String> stringTransformer = Object::toString;
    
    String result = stringTransformer.transform(lengthTransformer.transform("Test"));
    assertEquals("4", result);
  }

  @Test
  void testTransformer_withTypeConversion() throws Throwable {
    Transformer<Integer, Double> transformer = Integer::doubleValue;
    
    Double result = transformer.transform(42);
    assertEquals(42.0, result, 0.001);
  }

  @Test
  void testTransformer_identity() throws Throwable {
    Transformer<String, String> identity = input -> input;
    
    String input = "test";
    String result = identity.transform(input);
    assertSame(input, result);
  }

  @Test
  void testTransformer_withComplexObject() throws Throwable {
    record Person(String name, int age) {}
    
    Transformer<Person, String> transformer = p -> p.name() + " is " + p.age() + " years old";
    
    Person person = new Person("Alice", 30);
    String result = transformer.transform(person);
    assertEquals("Alice is 30 years old", result);
  }

  @Test
  void testTransformer_throwsCheckedException() {
    Transformer<String, Integer> transformer = input -> {
      throw new Exception("Checked exception");
    };
    
    assertThrows(Exception.class, () -> transformer.transform("test"));
  }

  @Test
  void testTransformer_withConditionalLogic() throws Throwable {
    Transformer<Integer, String> transformer = num -> {
      if (num < 0) return "negative";
      if (num == 0) return "zero";
      return "positive";
    };
    
    assertEquals("negative", transformer.transform(-5));
    assertEquals("zero", transformer.transform(0));
    assertEquals("positive", transformer.transform(5));
  }
}