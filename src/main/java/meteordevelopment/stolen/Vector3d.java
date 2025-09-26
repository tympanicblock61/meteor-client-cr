/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.stolen;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

import java.io.Serial;
import java.io.Serializable;

public class Vector3d extends Vector3 implements Serializable {
    @Serial
    private static final long serialVersionUID = 3840054589595372522L;
    public double x;
    public double y;
    public double z;
    public static final Vector3d X = new Vector3d(1.0F, 0.0F, 0.0F);
    public static final Vector3d Y = new Vector3d(0.0F, 1.0F, 0.0F);
    public static final Vector3d Z = new Vector3d(0.0F, 0.0F, 1.0F);
    public static final Vector3d Zero = new Vector3d(0.0F, 0.0F, 0.0F);
    private static final Matrix4 tmpMat = new Matrix4();

    public Vector3d() {
    }

    public Vector3d(double x, double y, double z) {
        this.set(x, y, z);
    }

    public Vector3d(Vector3d vector) {
        this.set(vector);
    }

    public Vector3d(double[] values) {
        this.set(values[0], values[1], values[2]);
    }

    public Vector3d(Vector2 vector, double z) {
        this.set(vector.x, vector.y, z);
    }

    public Vector3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3d set(Vector3d vector) {
        return this.set(vector.x, vector.y, vector.z);
    }

    public Vector3d set(double[] values) {
        return this.set(values[0], values[1], values[2]);
    }

    public Vector3d set(Vector2 vector, double z) {
        return this.set(vector.x, vector.y, z);
    }

    public Vector3d setFromSpherical(double azimuthalAngle, double polarAngle) {
        double cosPolar = MathUtils.cos((float) polarAngle);
        double sinPolar = MathUtils.sin((float) polarAngle);
        double cosAzim = MathUtils.cos((float) azimuthalAngle);
        double sinAzim = MathUtils.sin((float) azimuthalAngle);
        return this.set(cosAzim * sinPolar, sinAzim * sinPolar, cosPolar);
    }

    public Vector3d setToRandomDirection() {
        double u = MathUtils.random();
        double v = MathUtils.random();
        double theta = 6.2831855F * u;
        double phi = Math.acos(2.0 * v - 1.0);
        return this.setFromSpherical(theta, phi);
    }

    public Vector3d cpy() {
        return new Vector3d(this);
    }

    public Vector3d add(Vector3d vector) {
        return this.add(vector.x, vector.y, vector.z);
    }

    public Vector3d add(double x, double y, double z) {
        return this.set(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d add(double values) {
        return this.set(this.x + values, this.y + values, this.z + values);
    }

    public Vector3d sub(Vector3d a_vec) {
        return this.sub(a_vec.x, a_vec.y, a_vec.z);
    }

    public Vector3d sub(double x, double y, double z) {
        return this.set(this.x - x, this.y - y, this.z - z);
    }

    public Vector3d sub(double value) {
        return this.set(this.x - value, this.y - value, this.z - value);
    }

    public Vector3d scl(double scalar) {
        return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3d scl(Vector3d other) {
        return this.set(this.x * other.x, this.y * other.y, this.z * other.z);
    }

    public Vector3d scl(double vx, double vy, double vz) {
        return this.set(this.x * vx, this.y * vy, this.z * vz);
    }

    public Vector3d mulAdd(Vector3d vec, double scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        this.z += vec.z * scalar;
        return this;
    }

    public Vector3d mulAdd(Vector3d vec, Vector3d mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        this.z += vec.z * mulVec.z;
        return this;
    }

    public static double len(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public float len() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public static double len2(double x, double y, double z) {
        return x * x + y * y + z * z;
    }

    public float len2() {
        return (float) (this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public boolean idt(Vector3d vector) {
        return this.x == vector.x && this.y == vector.y && this.z == vector.z;
    }

    public static double dst(double x1, double y1, double z1, double x2, double y2, double z2) {
        double a = x2 - x1;
        double b = y2 - y1;
        double c = z2 - z1;
        return Math.sqrt(a * a + b * b + c * c);
    }

    public double dst(Vector3d vector) {
        double a = vector.x - this.x;
        double b = vector.y - this.y;
        double c = vector.z - this.z;
        return Math.sqrt(a * a + b * b + c * c);
    }

    public double dst(double x, double y, double z) {
        double a = x - this.x;
        double b = y - this.y;
        double c = z - this.z;
        return Math.sqrt(a * a + b * b + c * c);
    }

    public static double dst2(double x1, double y1, double z1, double x2, double y2, double z2) {
        double a = x2 - x1;
        double b = y2 - y1;
        double c = z2 - z1;
        return a * a + b * b + c * c;
    }

    public double dst2(Vector3d point) {
        double a = point.x - this.x;
        double b = point.y - this.y;
        double c = point.z - this.z;
        return a * a + b * b + c * c;
    }

    public double dst2(double x, double y, double z) {
        double a = x - this.x;
        double b = y - this.y;
        double c = z - this.z;
        return a * a + b * b + c * c;
    }

    public Vector3d nor() {
        double len2 = this.len2();
        return len2 != 0.0 && len2 != 1.0 ? this.scl(1.0 / Math.sqrt(len2)) : this;
    }

    public static double dot(double x1, double y1, double z1, double x2, double y2, double z2) {
        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    public double dot(Vector3d vector) {
        return this.x * vector.x + this.y * vector.y + this.z * vector.z;
    }

    public double dot(double x, double y, double z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public Vector3d crs(Vector3d vector) {
        return this.set(this.y * vector.z - this.z * vector.y, this.z * vector.x - this.x * vector.z, this.x * vector.y - this.y * vector.x);
    }

    public Vector3d crs(double x, double y, double z) {
        return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
    }

    public Vector3d mul4x3(double[] matrix) {
        return this.set(this.x * matrix[0] + this.y * matrix[3] + this.z * matrix[6] + matrix[9], this.x * matrix[1] + this.y * matrix[4] + this.z * matrix[7] + matrix[10], this.x * matrix[2] + this.y * matrix[5] + this.z * matrix[8] + matrix[11]);
    }

    public Vector3d mul(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12], this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13], this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]);
    }

    public Vector3d traMul(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2] + l_mat[3], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6] + l_mat[7], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10] + l_mat[11]);
    }

    public Vector3d mul(Matrix3 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[3] + this.z * l_mat[6], this.x * l_mat[1] + this.y * l_mat[4] + this.z * l_mat[7], this.x * l_mat[2] + this.y * l_mat[5] + this.z * l_mat[8]);
    }

    public Vector3d traMul(Matrix3 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[3] + this.y * l_mat[4] + this.z * l_mat[5], this.x * l_mat[6] + this.y * l_mat[7] + this.z * l_mat[8]);
    }

    public Vector3d mul(Quaternion quat) {
        return (Vector3d) quat.transform(this);
    }

    public Vector3d prj(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        double l_w = 1.0 / (this.x * l_mat[3] + this.y * l_mat[7] + this.z * l_mat[11] + l_mat[15]);
        return this.set((this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8] + l_mat[12]) * l_w, (this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9] + l_mat[13]) * l_w, (this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10] + l_mat[14]) * l_w);
    }

    public Vector3d rot(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[4] + this.z * l_mat[8], this.x * l_mat[1] + this.y * l_mat[5] + this.z * l_mat[9], this.x * l_mat[2] + this.y * l_mat[6] + this.z * l_mat[10]);
    }

    public Vector3d unrotate(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10]);
    }

    public Vector3d untransform(Matrix4 matrix) {
        float[] l_mat = matrix.val;
        this.x -= l_mat[12];
        this.y -= l_mat[12];
        this.z -= l_mat[12];
        return this.set(this.x * l_mat[0] + this.y * l_mat[1] + this.z * l_mat[2], this.x * l_mat[4] + this.y * l_mat[5] + this.z * l_mat[6], this.x * l_mat[8] + this.y * l_mat[9] + this.z * l_mat[10]);
    }

    public Vector3d rotate(double degrees, double axisX, double axisY, double axisZ) {
        return this.mul(tmpMat.setToRotation((float) axisX, (float) axisY, (float) axisZ, (float) degrees));
    }

    public Vector3d rotateRad(double radians, double axisX, double axisY, double axisZ) {
        return this.mul(tmpMat.setToRotationRad((float) axisX, (float) axisY, (float) axisZ, (float) radians));
    }

    public Vector3d rotate(Vector3d axis, double degrees) {
        tmpMat.setToRotation(axis, (float) degrees);
        return this.mul(tmpMat);
    }

    public Vector3d rotateRad(Vector3d axis, double radians) {
        tmpMat.setToRotationRad(axis, (float) radians);
        return this.mul(tmpMat);
    }

    public boolean isUnit() {
        return this.isUnit(1.0E-9F);
    }

    public boolean isUnit(double margin) {
        return Math.abs(this.len2() - 1.0) < margin;
    }

    public boolean isZero() {
        return this.x == 0.0 && this.y == 0.0 && this.z == 0.0;
    }

    public boolean isZero(double margin) {
        return this.len2() < margin;
    }

    public boolean isOnLine(Vector3d other, double epsilon) {
        return len2(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x) <= epsilon;
    }

    public boolean isOnLine(Vector3d other) {
        return len2(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z, this.x * other.y - this.y * other.x) <= 1.0E-6F;
    }

    public boolean isCollinear(Vector3d other, double epsilon) {
        return this.isOnLine(other, epsilon) && this.hasSameDirection(other);
    }

    public boolean isCollinear(Vector3d other) {
        return this.isOnLine(other) && this.hasSameDirection(other);
    }

    public boolean isCollinearOpposite(Vector3d other, double epsilon) {
        return this.isOnLine(other, epsilon) && this.hasOppositeDirection(other);
    }

    public boolean isCollinearOpposite(Vector3d other) {
        return this.isOnLine(other) && this.hasOppositeDirection(other);
    }

    public boolean isPerpendicular(Vector3d vector) {
        return MathUtils.isZero((float) this.dot(vector));
    }

    public boolean isPerpendicular(Vector3d vector, double epsilon) {
        return MathUtils.isZero((float) this.dot(vector), (float) epsilon);
    }

    public boolean hasSameDirection(Vector3d vector) {
        return this.dot(vector) > 0.0;
    }

    public boolean hasOppositeDirection(Vector3d vector) {
        return this.dot(vector) < 0.0;
    }

    public Vector3d lerp(Vector3d target, double alpha) {
        this.x += alpha * (target.x - this.x);
        this.y += alpha * (target.y - this.y);
        this.z += alpha * (target.z - this.z);
        return this;
    }

    public Vector3d interpolate(Vector3d target, double alpha, Interpolation interpolator) {
        return this.lerp(target, (double) interpolator.apply(0.0F, 1.0F, (float) alpha));
    }

    public Vector3d slerp(Vector3d target, double alpha) {
        double dot = this.dot(target);
        if (!(dot > 0.9995) && !(dot < -0.9995)) {
            double theta0 = Math.acos(dot);
            double theta = theta0 * alpha;
            double st = Math.sin(theta);
            double tx = target.x - this.x * dot;
            double ty = target.y - this.y * dot;
            double tz = target.z - this.z * dot;
            double l2 = tx * tx + ty * ty + tz * tz;
            double dl = st * (l2 < 1.0E-4F ? 1.0 : 1.0 / Math.sqrt(l2));
            return this.scl(Math.cos(theta)).add(tx * dl, ty * dl, tz * dl).nor();
        } else {
            return this.lerp(target, alpha);
        }
    }

    public String toString() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }

    public Vector3d fromString(String v) {
        int s0 = v.indexOf(44, 1);
        int s1 = v.indexOf(44, s0 + 1);
        if (s0 != -1 && s1 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
            try {
                double x = Float.parseFloat(v.substring(1, s0));
                double y = Float.parseFloat(v.substring(s0 + 1, s1));
                double z = Float.parseFloat(v.substring(s1 + 1, v.length() - 1));
                return this.set(x, y, z);
            } catch (NumberFormatException ignored) {
            }
        }

        throw new GdxRuntimeException("Malformed Vector3: " + v);
    }

    public Vector3d limit(double limit) {
        return this.limit2(limit * limit);
    }

    public Vector3d limit2(double limit2) {
        double len2 = this.len2();
        if (len2 > limit2) {
            this.scl(Math.sqrt(limit2 / len2));
        }

        return this;
    }

    public Vector3d setLength(double len) {
        return this.setLength2(len * len);
    }

    public Vector3d setLength2(double len2) {
        double oldLen2 = this.len2();
        return oldLen2 != 0.0 && oldLen2 != len2 ? this.scl(Math.sqrt(len2 / oldLen2)) : this;
    }

    public Vector3d clamp(double min, double max) {
        double len2 = this.len2();
        if (len2 == 0.0) {
            return this;
        } else {
            double max2 = max * max;
            if (len2 > max2) {
                return this.scl(Math.sqrt(max2 / len2));
            } else {
                double min2 = min * min;
                return len2 < min2 ? this.scl(Math.sqrt(min2 / len2)) : this;
            }
        }
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + NumberUtils.floatToIntBits((float) this.x);
        result = 31 * result + NumberUtils.floatToIntBits((float) this.y);
        result = 31 * result + NumberUtils.floatToIntBits((float) this.z);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Vector3d other = (Vector3d)obj;
            if (NumberUtils.floatToIntBits((float) this.x) != NumberUtils.floatToIntBits((float) other.x)) {
                return false;
            } else if (NumberUtils.floatToIntBits((float) this.y) != NumberUtils.floatToIntBits((float) other.y)) {
                return false;
            } else {
                return NumberUtils.floatToIntBits((float) this.z) == NumberUtils.floatToIntBits((float) other.z);
            }
        }
    }

    public boolean epsilonEquals(Vector3d other, double epsilon) {
        if (other == null) {
            return false;
        } else if (Math.abs(other.x - this.x) > epsilon) {
            return false;
        } else if (Math.abs(other.y - this.y) > epsilon) {
            return false;
        } else {
            return !(Math.abs(other.z - this.z) > epsilon);
        }
    }

    public boolean epsilonEquals(double x, double y, double z, double epsilon) {
        if (Math.abs(x - this.x) > epsilon) {
            return false;
        } else if (Math.abs(y - this.y) > epsilon) {
            return false;
        } else {
            return !(Math.abs(z - this.z) > epsilon);
        }
    }

    public boolean epsilonEquals(Vector3d other) {
        return this.epsilonEquals(other, 1.0E-6);
    }

    public boolean epsilonEquals(double x, double y, double z) {
        return this.epsilonEquals(x, y, z, 1.0E-6);
    }

    public Vector3d setZero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        return this;
    }
}
