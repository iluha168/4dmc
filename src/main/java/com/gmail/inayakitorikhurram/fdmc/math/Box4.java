package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public class Box4 extends Box {
    public final double minX4;
    public final double maxX4;
    public final double minW;
    public final double maxW;

    public Box4(
        double startX, double startX4, double startY, double startZ, double startW,
        double endX  , double endX4  , double endY  , double endZ  , double endW
    ) {
//        super (
//            startX, startY, startZ,
//            endX  , endY  , endZ
//        );
        super (
            startX, startY, startZ,
            startX + endX4 - startX4, endY, endZ
        );
        this.minX4 = Math.min(startX4, endX4);
        this.maxX4 = Math.max(startX4, endX4);
        this.minW  = Math.min(startW , endW );
        this.maxW  = Math.max(startW , endW );
    }

    public Box4(
        double startX4, double startY, double startZ, double startW,
        double endX4  , double endY  , double endZ  , double endW
    ) {
        this (
            startX4 + FDMCMath.getOffsetX(Math.round(startW)), startX4, startY, startZ, startW,
            endX4   + FDMCMath.getOffsetX(Math.round(endW)), endX4  , endY  , endZ  , endW
        );
    }

    public Box4(Vec4d start, Vec4d end) {
        this (
            start.x, start.x4, start.y, start.z, start.w,
            end  .x, end.x4 , end  .y, end  .z , end.w
        );
    }

    public Box4(Vec4d pos) {
        this(
            pos.x, pos.x4, pos.y, pos.z, pos.w,
            pos.x+1, pos.x4+1, pos.y+1, pos.z+1, pos.w+1
        );
    }

    public Box4(BlockPos4<?, ?> pos) {
        this(Vec4d.of(pos));
    }

    public static Box4 of(Box box) {
        return box instanceof Box4 box4
            ? box4
            : new Box4(Vec4d.of(box.getMinPos()), Vec4d.of(box.getMaxPos()));
    }

    public static Box4 converted(Box box) {
        return Box4.of(box);
    }

    /**
     * Removes W coordinates from box. Just like {@link Box4#getSlice(int)}, but for 0, and faster.
     */
    public Box flatten() {
        return new Box(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    @Override
    public Box4 withMinX(double minX) {
        double[] X4W = FDMCMath.splitX3(minX);
        return new Box4(minX, X4W[0], minY, minZ, X4W[1], maxX, maxX4, maxY, maxZ, maxW);
    }
    @Override
    public Box4 withMinY(double minY) {
        return new Box4(minX, minX4, minY, minZ, minW, maxX, maxX4, maxY, maxZ, maxW);
    }
    @Override
    public Box4 withMinZ(double minZ) {
        return new Box4(minX, minX4, minY, minZ, minW, maxX, maxX4, maxY, maxZ, maxW);
    }

    @Override
    public Box4 withMaxX(double maxX) {
        double[] X4W = FDMCMath.splitX3(maxX);
        return new Box4(minX, minX4, minY, minZ, minW, maxX, X4W[0], maxY, maxZ, X4W[1]);
    }
    @Override
    public Box4 withMaxY(double maxY) {
        return new Box4(minX, minX4, minY, minZ, minW, maxX, maxX4, maxY, maxZ, maxW);
    }
    @Override
    public Box4 withMaxZ(double maxZ) {
        return new Box4(minX, minX4, minY, minZ, minW, maxX, maxX4, maxY, maxZ, maxW);
    }

    @Override
    public double getMin(Direction.Axis axis) {
        // minX instead of minX4 is intentional
        return Direction4.Axis4.asAxis4(axis).choose(this.minX, this.minY, this.minZ, this.minW);
    }
    @Override
    public double getMax(Direction.Axis axis) {
        // maxX instead of maxX4 is intentional
        return Direction4.Axis4.asAxis4(axis).choose(this.maxX, this.maxY, this.maxZ, this.maxW);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (o instanceof Box4 box4) {
            if (Double.compare(box4.minX4, this.minX4) != 0) {
                return false;
            }
            if (Double.compare(box4.minW, this.minW) != 0) {
                return false;
            }
            if (Double.compare(box4.maxX4, this.maxX4) != 0) {
                return false;
            }
	        return Double.compare(box4.maxW, this.maxW) == 0;
        }
        return true;
    }

    @Override
    public int hashCode() {
	    int hashCode = super.hashCode();
        hashCode = 31 * hashCode + Double.hashCode(this.maxX4);
        hashCode = 31 * hashCode + Double.hashCode(this.minX4);
        hashCode = 31 * hashCode + Double.hashCode(this.maxW);
        hashCode = 31 * hashCode + Double.hashCode(this.minW);
        return hashCode;
    }

    @Override
    public Box shrink(double dx, double dy, double dz) {
        // These invocations should be mixin
        return super.shrink(dx, dy, dz);
    }

    @Override
    public Box stretch(Vec3d scale3) {
        if (scale3 instanceof Vec4d scale) {
            return this.stretch(scale.x4, scale.y, scale.z, scale.w);
        }
        return super.stretch(scale3);
    }

    @Override
    public Box stretch(double dx, double dy, double dz) {
        // These invocations should be mixin
        return super.stretch(dx, dy, dz);
    }

    public Box4 stretch(double dx, double dy, double dz, double dw) {
        double newMinX = this.minX4;
        double newMinY = this.minY;
        double newMinZ = this.minZ;
        double newMinW = this.minW;
        double newMaxX = this.maxX4;
        double newMaxY = this.maxY;
        double newMaxZ = this.maxZ;
        double newMaxW = this.maxW;
        if (dx < 0.0) {
            newMinX += dx;
        } else if (dx > 0.0) {
            newMaxX += dx;
        }
        if (dy < 0.0) {
            newMinY += dy;
        } else if (dy > 0.0) {
            newMaxY += dy;
        }
        if (dz < 0.0) {
            newMinZ += dz;
        } else if (dz > 0.0) {
            newMaxZ += dz;
        }
        if (dw < 0.0) {
            newMinW += dw;
        } else if (dz > 0.0) {
            newMaxW += dw;
        }
        return new Box4(newMinX, newMinY, newMinZ, newMinW, newMaxX, newMaxY, newMaxZ, newMaxW);
    }

    @Override
    public Box expand(double x, double y, double z) {
        // These invocations should be mixin
        return super.expand(x, y, z);
    }

    public Box4 expand(double x, double y, double z, double w) {
        return new Box4(
            this.minX4 - x,
            this.minY - y,
            this.minZ - z,
            this.minW - w,
            this.maxX4 + x,
            this.maxY + y,
            this.maxZ + z,
            this.maxW + w
        );
    }

    @Override
    public Box4 expand(double value) {
        return this.expand(value, value, value, value);
    }

    @Override
    public Box intersection(Box box3) {
        if (box3 instanceof Box4 box) {
            return new Box4(
                Math.max(this.minX, box.minX),
                Math.max(this.minX4, box.minX4),
                Math.max(this.minY, box.minY),
                Math.max(this.minZ, box.minZ),
                Math.max(this.minW, box.minW),
                Math.min(this.maxX, box.maxX),
                Math.min(this.maxX4, box.maxX4),
                Math.min(this.maxY, box.maxY),
                Math.min(this.maxZ, box.maxZ),
                Math.min(this.maxW, box.maxW)
            );
        }
        return super.intersection(box3);
    }

    @Override
    public Box union(Box box3) {
        if (box3 instanceof Box4 box) {
            return new Box4(
                Math.min(this.minX, box.minX),
                Math.min(this.minX4, box.minX4),
                Math.min(this.minY, box.minY),
                Math.min(this.minZ, box.minZ),
                Math.min(this.minW, box.minW),
                Math.max(this.maxX, box.maxX),
                Math.max(this.maxX4, box.maxX4),
                Math.max(this.maxY, box.maxY),
                Math.max(this.maxZ, box.maxZ),
                Math.max(this.maxW, box.maxW)
            );
        }
        return super.union(box3);
    }

    @Override
    public Box offset(double x, double y, double z) {
        // These invocations should be mixin
        return super.offset(x, y, z);
    }

    public Box offset(double x, double y, double z, double w) {
        return this.offset(new Vec4d(x, y, z, w));
    }

    @Override
    public Box4 offset(BlockPos blockPos3) {
        BlockPos4<?, ?> blockPos = BlockPos4.of(blockPos3);
        return new Box4(
            this.minX + (double)blockPos.getX(),
            this.minX4 + (double)blockPos.getX4(),
            this.minY + (double)blockPos.getY4(),
            this.minZ + (double)blockPos.getZ4(),
            this.minW + (double)blockPos.getW4(),
            this.maxX + (double)blockPos.getX(),
            this.maxX4 + (double)blockPos.getX4(),
            this.maxY + (double)blockPos.getY4(),
            this.maxZ + (double)blockPos.getZ4(),
            this.maxW + (double)blockPos.getW4()
        );
    }

    @Override
    public Box offset(Vec3d vec3) {
        if (vec3 instanceof Vec4d vec) {
            return new Box4(
                this.minX + vec.x, this.minX4 + vec.x4, this.minY + vec.y, this.minZ + vec.z, this.minW + vec.w,
                this.maxX + vec.x, this.maxX4 + vec.x4, this.maxY + vec.y, this.maxZ + vec.z, this.maxW + vec.w
            );
        }
        return super.offset(vec3);
    }

    @Override
    public Box offset(Vector3f offset) {
        // These invocations should be mixin
        return this.offset(offset.x, offset.y, offset.z);
    }

    @Override
    public boolean intersects(Box box3) {
        if (box3 instanceof Box4 box)
            return this.intersects(box.minX4, box.minY, box.minZ, box.minW, box.maxX4, box.maxY, box.maxZ, box.maxW);
        return super.intersects(box3);
    }

    @Override
    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return super.intersects(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public boolean intersects(double minX4, double minY, double minZ, double minW, double maxX4, double maxY, double maxZ, double maxW) {
        return this.minX4 < maxX4 && this.maxX4 > minX4 && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ && this.minW < maxW && this.maxW > minW;
    }

    @Override
    public boolean intersects(Vec3d pos13, Vec3d pos23) {
        if (pos13 instanceof Vec4d pos1 && pos23 instanceof Vec4d pos2) {
            return this.intersects(
                Math.min(pos1.x4, pos2.x4),
                Math.min(pos1.y, pos2.y),
                Math.min(pos1.z, pos2.z),
                Math.min(pos1.w, pos2.w),
                Math.max(pos1.x4, pos2.x4),
                Math.max(pos1.y, pos2.y),
                Math.max(pos1.z, pos2.z),
                Math.max(pos1.w, pos2.w)
            );
        }
        return super.intersects(pos13, pos23);
    }

    @Override
    public boolean contains(BlockPos pos3) {
        BlockPos4<?, ?> pos = BlockPos4.of(pos3);
        return this.intersects(
            pos.getX4(), pos.getY4(), pos.getZ4(), pos.getW4(),
            pos.getX4()+1, pos.getY4()+1, pos.getZ4()+1, pos.getW4()+1
        );
    }

    @Override
    public boolean contains(Vec3d pos3) {
        if (pos3 instanceof Vec4d pos) {
            return this.contains(pos.x4, pos.y, pos.z, pos.w);
        }
        return super.contains(pos3);
    }

    @Override
    public boolean contains(double x, double y, double z) {
        // These invocations should be mixin
        return super.contains(x, y, z);
    }

    public boolean contains(double x, double y, double z, double w) {
        return x >= this.minX4 && x < this.maxX4 && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ && w >= this.minW && w < this.maxW;
    }

    @Override
    public double getAverageSideLength() {
        return (
            this.getLengthX4()
                + this.getLengthY()
                + this.getLengthZ()
                + this.getLengthW()
        ) / 4d;
    }

    @Override
    public double getLengthX() {
        return super.getLengthX();
    }
    public double getLengthX4() {
        return this.maxX4 - this.minX4;
    }
    @Override
    public double getLengthY() {
        return super.getLengthY();
    }
    @Override
    public double getLengthZ() {
        return super.getLengthZ();
    }
    public double getLengthW() {
        return this.maxW - this.minW;
    }

    @Override
    public Box contract(double x, double y, double z) {
        // These invocations should be mixin
        return super.contract(x, y, z);
    }
    @Override
    public Box4 contract(double value) {
        return this.expand(-value);
    }

    @Nullable
    private static Direction4 traceCollisionSide(
        double[] traceDistanceResult, @Nullable Direction4 approachDirection,
        double deltaX, double deltaY, double deltaZ, double deltaW,
        double begin,
        double minX, double maxX,
        double minZ, double maxZ,
        double minW, double maxW,
        Direction4 resultDirection,
        double startX, double startY, double startZ, double startW
    ) {
        double x = (begin - startX) / deltaX;
        double y = startY + x * deltaY;
        double z = startZ + x * deltaZ;
        double w = startW + x * deltaW;
        if (
            0d < x && x < traceDistanceResult[0]
                && minX - 1E-7 < y && y < maxX + 1E-7
                && minZ - 1E-7 < z && z < maxZ + 1E-7
                && minW - 1E-7 < w && w < maxW + 1E-7
        ) {
            traceDistanceResult[0] = x;
            return resultDirection;
        }
        return approachDirection;
    }

    @Nullable
    private static Direction4 traceCollisionSide(
        double minX, double minY, double minZ, double minW,
        double maxX, double maxY, double maxZ, double maxW,
        Vec4d intersectingVector,
        double[] traceDistanceResult,
        @Nullable Direction4 approachDirection,
        double deltaX, double deltaY, double deltaZ, double deltaW
    ) {
        if (deltaX > 1E-7) {
            approachDirection = Box4.traceCollisionSide(traceDistanceResult, approachDirection, deltaX, deltaY, deltaZ, deltaW, minX, minY, maxY, minZ, maxZ, minW, maxW, Direction4Constants.WEST4, intersectingVector.x4, intersectingVector.y, intersectingVector.z, intersectingVector.w);
        } else if (deltaX < -1E-7) {
            approachDirection = Box4.traceCollisionSide(traceDistanceResult, approachDirection, deltaX, deltaY, deltaZ, deltaW, maxX, minY, maxY, minZ, maxZ, minW, maxW, Direction4Constants.EAST4, intersectingVector.x4, intersectingVector.y, intersectingVector.z, intersectingVector.w);
        }
        if (deltaY > 1E-7) {
            approachDirection = Box4.traceCollisionSide(traceDistanceResult, approachDirection, deltaY, deltaZ, deltaX, deltaW, minY, minW, maxW, minZ, maxZ, minX, maxX, Direction4Constants.DOWN4, intersectingVector.y, intersectingVector.w, intersectingVector.z, intersectingVector.x4);
        } else if (deltaY < -1E-7) {
            approachDirection = Box4.traceCollisionSide(traceDistanceResult, approachDirection, deltaY, deltaZ, deltaX, deltaW, maxY, minW, maxW, minZ, maxZ, minX, maxX, Direction4Constants.UP4, intersectingVector.y, intersectingVector.w, intersectingVector.z, intersectingVector.x4);
        }
        if (deltaZ > 1E-7) {
            approachDirection = Box4.traceCollisionSide(traceDistanceResult, approachDirection, deltaZ, deltaX, deltaY, deltaW, minZ, minX, maxX, minY, maxY, minW, maxW, Direction4Constants.NORTH4, intersectingVector.z, intersectingVector.x4, intersectingVector.y, intersectingVector.w);
        } else if (deltaZ < -1E-7) {
            approachDirection = Box4.traceCollisionSide(traceDistanceResult, approachDirection, deltaZ, deltaX, deltaY, deltaW, maxZ, minX, maxX, minY, maxY, minW, maxW, Direction4Constants.SOUTH4, intersectingVector.z, intersectingVector.x4, intersectingVector.y, intersectingVector.w);
        }
        if (deltaW > 1E-7) {
            approachDirection = Box4.traceCollisionSide(traceDistanceResult, approachDirection, deltaW, deltaX, deltaY, deltaZ, minW, minX, maxX, minY, maxY, minZ, maxZ, Direction4Constants.KATA4, intersectingVector.w, intersectingVector.x4, intersectingVector.y, intersectingVector.z);
        } else if (deltaW < -1E-7) {
            approachDirection = Box4.traceCollisionSide(traceDistanceResult, approachDirection, deltaW, deltaX, deltaY, deltaZ, maxW, minX, maxX, minY, maxY, minZ, maxZ, Direction4Constants.ANA4, intersectingVector.w, intersectingVector.x4, intersectingVector.y, intersectingVector.z);
        }
        return approachDirection;
    }

    public static Optional<Vec4d> raycast(
        double minX, double minY, double minZ, double minW,
        double maxX, double maxY, double maxZ, double maxW,
        Vec4d from, Vec4d to
    ) {
        double[] traceDistanceResultRef = new double[]{1.0};

        double dx = to.x4 - from.x4;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double dw = to.w - from.w;

        Direction4 direction = Box4.traceCollisionSide(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW, from, traceDistanceResultRef, null, dx, dy, dz, dw);

        if (direction == null) {
            return Optional.empty();
        }
        double traceDistanceResult = traceDistanceResultRef[0];
        return Optional.of(from.add(traceDistanceResult * dx, traceDistanceResult * dy, traceDistanceResult * dz, traceDistanceResult * dw));
    }

    @Override
    public Optional<Vec3d> raycast(Vec3d from, Vec3d to) {
        return Box4.raycast(this.minX, this.minY, this.minZ, this.minW, this.maxX, this.maxY, this.maxZ, this.maxW, Vec4d.of(from), Vec4d.of(to))
            .map(vec4d -> vec4d);
    }

    @Override
    public boolean collides(Vec3d pos3, List<Box> boundingBoxes) {
        if (pos3 instanceof Vec4d pos) {
            Vec4d center = this.getCenter();
            Vec4d offsetCenter = center.add(pos);
            for (Box box : boundingBoxes) {
                Box4 boxOther = Box4.of(box).expand(
                    this.getLengthX4() * 0.5 - 1.0E-7,
                    this.getLengthY() * 0.5 - 1.0E-7,
                    this.getLengthZ() * 0.5 - 1.0E-7,
                    this.getLengthW() * 0.5 - 1.0E-7
                );
                if (boxOther.contains(offsetCenter) || boxOther.contains(center)) {
                    return true;
                }
                if (boxOther.raycast(center, offsetCenter).isEmpty()) continue;
                return true;
            }
            return false;
        }
        return super.collides(pos3, boundingBoxes);
    }

    @Override
    public double squaredMagnitude(Vec3d pos3) {
        if (pos3 instanceof Vec4d pos) {
            return FDMCMath.squaredMagnitude(
                Math.max(Math.max(this.minX4 - pos.x4, pos.x4 - this.maxX4), 0.0),
                Math.max(Math.max(this.minY - pos.y, pos.y - this.maxY), 0.0),
                Math.max(Math.max(this.minZ - pos.z, pos.z - this.maxZ), 0.0),
                Math.max(Math.max(this.minW - pos.w, pos.w - this.maxW), 0.0)
            );
        }
        return super.squaredMagnitude(pos3);
    }

    @Override
    public double squaredMagnitude(Box other3) {
        Box4 other = Box4.of(other3);
        return FDMCMath.squaredMagnitude(
            Math.max(Math.max(this.minX4 - other.maxX4, other.minX4 - this.maxX4), 0.0),
            Math.max(Math.max(this.minY - other.maxY, other.minY - this.maxY), 0.0),
            Math.max(Math.max(this.minZ - other.maxZ, other.minZ - this.maxZ), 0.0),
            Math.max(Math.max(this.minW - other.maxW, other.minW - this.maxW), 0.0)
        );
    }

    @Override
    public String toString() {
        return "AABB4["
            + this.minX4 + ", " + this.minY + ", " + this.minZ + ", " + this.minW
            + "] -> ["
            + this.maxX4 + ", " + this.maxY + ", " + this.maxZ + ", " + this.maxW
            + "]";
    }

    @Override
    public boolean isNaN() {
        return super.isNaN()
            || Double.isNaN(this.minX4) || Double.isNaN(this.maxX4)
            || Double.isNaN(this.minW ) || Double.isNaN(this.maxW );
    }

    @Override
    public Vec4d getCenter() {
        return new Vec4d(
            MathHelper.lerp(0.5, this.minX4, this.maxX4),
            MathHelper.lerp(0.5, this.minY, this.maxY),
            MathHelper.lerp(0.5, this.minZ, this.maxZ),
            MathHelper.lerp(0.5, this.minW, this.maxW)
        );
    }

    @Override
    public Vec4d getHorizontalCenter() {
        return new Vec4d(
            MathHelper.lerp(0.5, this.minX4, this.maxX4),
            this.minY,
            MathHelper.lerp(0.5, this.minZ, this.maxZ),
            MathHelper.lerp(0.5, this.minW, this.maxW)
        );
    }

    @Override
    public Vec4d getMinPos() {
        return new Vec4d(this.minX4, this.minY, this.minZ, this.minW);
    }

    @Override
    public Vec4d getMaxPos() {
        return new Vec4d(this.maxX4, this.maxY, this.maxZ, this.maxW);
    }

    // ------------------------------- BRAND NEW METHODS
    /**
     * For ease with working with situations that only check box3 values, will shift those values to be in the appropriate w range
     */
    public Box getSlice(int w){
        Vec3d min = getMinPos().withAxis(Direction4Constants.Axis4Constants.W, w);
        Vec3d max = getMaxPos().withAxis(Direction4Constants.Axis4Constants.W, w);
        return new Box(min, max);
    }

    public ImmutableList<Box> slices(){
        int minW = MathHelper.floor(this.minW);
        int maxW = MathHelper.ceil(this.maxW);
        //FDMCConstants.LOGGER.info("checking box4 slices between {} <= w < {}", minW, maxW);
        ImmutableList.Builder<Box> list = ImmutableList.builder();
        for (int w = minW; w < maxW; w++) {
            // This function only looks at the
            list.add(this.getSlice(w));
        }
        return list.build();
    }
}
