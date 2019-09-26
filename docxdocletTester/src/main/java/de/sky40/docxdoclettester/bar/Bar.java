/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sky40.docxdoclettester.bar;

/**
 * Another class for testing purpose.
 * 
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 * @version 1.1
 */
public class Bar {
  
  /**
   * Thou shall not make fields public!
   */
  public BarEnum myEnum;

  
  /**
   * Constructor with a comment.
   * @param myEnum the enum value
   */
  public Bar(BarEnum myEnum) {
    this.myEnum = myEnum;
  }

  public Bar() {
  }

  public BarEnum getMyEnum() {
    return myEnum;
  }
    
}
