package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class Project extends Controller {

    public static void createNew() {
        render();
    }    

    public static void createNew2() {
        render();
    }

    
}