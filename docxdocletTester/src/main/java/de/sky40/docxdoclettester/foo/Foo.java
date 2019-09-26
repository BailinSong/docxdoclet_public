package de.sky40.docxdoclettester.foo;

/**
 * A class to implement the {@link IFoo interface}
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class Foo extends AbstractFoo implements IFoo, IAnotherFoo {

  /**
   * Its a Foo!
   */
  public Foo() {
    super();
  }

  
  @Override
  public int doAnotherFoo(String stuff) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String doFooStuff(int i) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * A public method of foo.
   * @param i some input
   * @return some string
   */
  public String doSomeOtherStuff(String i) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int doOtherStuff(String stringArg, long longArg) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
