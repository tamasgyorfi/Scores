package hu.bets.points.processor;

interface Typed {
    String getType();
}

public enum Type implements Typed {
    BETS_REQUEST {
        @Override
        public String getType() {
            return this.name();
        }
    },
    RETRY_REQUEST {
        @Override
        public String getType() {
            return BETS_REQUEST.name();
        }
    },
    ACKNOWLEDGE_REQUEST {
        @Override
        public String getType() {
            return this.name();
        }
    }
}
