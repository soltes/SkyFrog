/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import play.mvc.With;

@With(Secure.class)
public class Tasks extends CRUD {

}
