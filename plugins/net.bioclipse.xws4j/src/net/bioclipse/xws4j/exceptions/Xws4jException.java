package net.bioclipse.xws4j.exceptions;
public class Xws4jException extends Exception {
        private static final long serialVersionUID = -4294651006065532451L;
        public Xws4jException() {
                super();
        }
        public Xws4jException(String message) {
                super(message);
        }
        public Xws4jException(Throwable t) {
                super(t);
        }
}