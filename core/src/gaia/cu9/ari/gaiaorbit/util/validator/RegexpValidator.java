package gaia.cu9.ari.gaiaorbit.util.validator;

public class RegexpValidator extends CallbackValidator {
    private String expr;

    public RegexpValidator(String expression) {
        this(null, expression);
    }

    public RegexpValidator(IValidator parent, String expression) {
        super(parent);
        this.expr = expression;
    }

    @Override
    protected boolean validateLocal(String value) {
        return value.matches(expr);
    }

}
