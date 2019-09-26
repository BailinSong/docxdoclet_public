package de.sky40.docxdoclettester.foo;

import de.sky40.docxdoclettester.ex.FooException;

/**
 * An abstract class example to play with.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public abstract class AbstractFoo implements IFoo {

  /**
   * An abstract method with two parameters.
   *
   * @param stringArg A string argument.
   * @param longArg A long argument.
   * @return Some int value.
   * @throws FooException In case of foo happened.
   */
  abstract public int doOtherStuff(String stringArg, long longArg) throws FooException, RuntimeException;

}
