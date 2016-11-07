package com.yy.opensource.coreanimation;

/**
 * Created by cuiminghui on 2016/11/7.
 */

public class CABasicAnimation<T> extends CAPropertyAnimation {

    public T fromValue;
    public T toValue;

    @Override
    protected void duration(long currentTimeMillis) {
        super.duration(currentTimeMillis);
        resetFromValue();
        if (Number.class.isAssignableFrom(fromValue.getClass()) && Number.class.isAssignableFrom(toValue.getClass())) {
            float _fromValue = ((Number)fromValue).floatValue();
            float _toValue = ((Number)toValue).floatValue();
            if (duration == 0.0) {
                setValue(_toValue);
                end(true);
                return;
            }
            float progress = Math.min(1.0f, currentTimeMillis / (duration * 1000));
            float _newValue = _fromValue + (_toValue - _fromValue) * progress;
            setValue(_newValue);
            if (progress == 1.0) {
                end(true);
            }
        }
        else if (CGRect.class.isAssignableFrom(fromValue.getClass()) && CGRect.class.isAssignableFrom(toValue.getClass())) {
            CGRect _fromValue = (CGRect)fromValue;
            CGRect _toValue = (CGRect)toValue;
            if (duration == 0.0) {
                setValue(_toValue);
                end(true);
                return;
            }
            float progress = Math.min(1.0f, currentTimeMillis / (duration * 1000));
            CGRect _newValue = new CGRect(
                    _fromValue.x + (_toValue.x - _fromValue.x) * progress,
                    _fromValue.y + (_toValue.y - _fromValue.y) * progress,
                    _fromValue.width + (_toValue.width - _fromValue.width) * progress,
                    _fromValue.height + (_toValue.height - _fromValue.height) * progress
            );
            setValue(_newValue);
            if (progress == 1.0) {
                end(true);
            }
        }
        else if (CATransform3D.class.isAssignableFrom(fromValue.getClass()) && CATransform3D.class.isAssignableFrom(toValue.getClass())) {
            CATransform3D _fromValue = (CATransform3D)fromValue;
            CATransform3D _toValue = (CATransform3D)toValue;
            if (duration == 0.0) {
                setValue(_toValue);
                end(true);
                return;
            }
            float progress = Math.min(1.0f, currentTimeMillis / (duration * 1000));
            CATransform3D _newValue = new CATransform3D();
            _newValue.a = _fromValue.a + (_toValue.a - _fromValue.a) * progress;
            _newValue.b = _fromValue.b + (_toValue.b - _fromValue.b) * progress;
            _newValue.c = _fromValue.c + (_toValue.c - _fromValue.c) * progress;
            _newValue.d = _fromValue.d + (_toValue.d - _fromValue.d) * progress;
            _newValue.tx = _fromValue.tx + (_toValue.tx - _fromValue.tx) * progress;
            _newValue.ty = _fromValue.ty + (_toValue.ty - _fromValue.ty) * progress;
            setValue(_newValue);
            if (progress == 1.0) {
                end(true);
            }
        }
    }

    protected void resetFromValue() {
        if (fromValue == null) {
            if (keyPath.equalsIgnoreCase("transform")) {
                fromValue = (T) layer.transform;
            }
            else if (keyPath.equalsIgnoreCase("frame")) {
                fromValue = (T) layer.frame;
            }
            else if (keyPath.equalsIgnoreCase("opacity")) {
                fromValue = (T) new CANumber(layer.opacity);
            }
            else if (keyPath.equalsIgnoreCase("frame.x")) {
                fromValue = (T) new CANumber(layer.frame.x);
            }
            else if (keyPath.equalsIgnoreCase("frame.y")) {
                fromValue = (T) new CANumber(layer.frame.y);
            }
            else if (keyPath.equalsIgnoreCase("frame.width")) {
                fromValue = (T) new CANumber(layer.frame.width);
            }
            else if (keyPath.equalsIgnoreCase("frame.height")) {
                fromValue = (T) new CANumber(layer.frame.height);
            }
        }
    }

    protected void setValue(CATransform3D newValue) {
        if (keyPath.equalsIgnoreCase("transform")) {
            layer.transform = newValue;
            layer.setNeedsDisplay();
        }
    }

    protected void setValue(CGRect newValue) {
        if (keyPath.equalsIgnoreCase("frame")) {
            layer.frame = newValue;
            layer.setNeedsDisplay();
        }
    }

    protected void setValue(float newValue) {
        if (keyPath.equalsIgnoreCase("opacity")) {
            layer.opacity = newValue;
            layer.setNeedsDisplay();
        }
        else if (keyPath.equalsIgnoreCase("frame.x")) {
            layer.frame.x = newValue;
            layer.setNeedsDisplay();
        }
        else if (keyPath.equalsIgnoreCase("frame.y")) {
            layer.frame.y = newValue;
            layer.setNeedsDisplay();
        }
        else if (keyPath.equalsIgnoreCase("frame.width")) {
            layer.frame.width = newValue;
            layer.setNeedsDisplay();
        }
        else if (keyPath.equalsIgnoreCase("frame.height")) {
            layer.frame.height = newValue;
            layer.setNeedsDisplay();
        }
    }


}

class CANumber extends Number {

    float mValue;

    public CANumber(float value) {
        mValue = value;
    }

    @Override
    public int intValue() {
        return (int)mValue;
    }

    @Override
    public long longValue() {
        return (long)mValue;
    }

    @Override
    public float floatValue() {
        return mValue;
    }

    @Override
    public double doubleValue() {
        return (double)mValue;
    }

}