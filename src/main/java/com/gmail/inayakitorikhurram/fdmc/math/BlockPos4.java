package com.gmail.inayakitorikhurram.fdmc.math;

import com.gmail.inayakitorikhurram.fdmc.mixininterfaces.Direction4;
import com.gmail.inayakitorikhurram.fdmc.util.UtilConstants;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.Function;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;

public interface BlockPos4<E extends BlockPos4<E, T>, T extends BlockPos> extends Vec4i<E, T> {


    static BlockPos4<?, ?> newBlockPos4(int x, int y, int z, int w) {
        return UtilConstants.ORIGIN4.newInstance(x, y, z, w);
    }

    static BlockPos4<?, ?> newBlockPos4(double x, double y, double z, double w) {
        return UtilConstants.ORIGIN4.newInstance(x, y, z, w);
    }

    static BlockPos4<?, ?> from3i(int x, int y, int z) {
        int[] xw4 = FDMCMath.splitX3(x);
        return newBlockPos4(xw4[0], y, z, xw4[1]);
    }

    static BlockPos4<?, ?> fromVec3i(Vec3i vec3i) {
        return fromVec4i(Vec4i.of(vec3i));
    }

    static BlockPos4<?, ?> fromVec4i(Vec4i<?, ?> vec4i) {
        return newBlockPos4(vec4i.getX4(), vec4i.getY4(), vec4i.getZ4(), vec4i.getW4());
    }

    static BlockPos4<?, ?> of(BlockPos pos) {
        return (BlockPos4<?, ?>)(Object) pos;
    }



    static Iterable<BlockPos> iterateOutwardsModification(BlockPos4<?,?> center, Function<Integer, Iterator<BlockPos>> offsetIterator, int rangeW) {
        Iterator<BlockPos> list = Collections.emptyIterator();
        for(int absw = 0; absw <= rangeW; absw++){
            for(int side = -1; side <= 1; side++){
                if(absw == 0 && side == 1) continue;
                int dw = absw * side;
                list = Iterators.concat(list, offsetIterator.apply(dw));
            }
        }
        Iterator<BlockPos> finalList = list;
        return new Iterable<BlockPos>() {
            @Override
            public @NotNull Iterator<BlockPos> iterator() {
                return finalList;
            }
        };
    }

    default BlockPos asBlockPos() {
        return (BlockPos)(Object) this;
    }

    default Vec4d toCenterPos4() {
        return Vec4d.ofCenter(this);
    }

    default E rotate4(BlockRotation rotation) {
        switch (rotation) {
            default: {
                return self();
            }
            case CLOCKWISE_90: {
                return newInstance(-this.getZ4(), this.getY4(), this.getX4(), this.getW4());
            }
            case CLOCKWISE_180: {
                return newInstance(-this.getX4(), this.getY4(), -this.getZ4(), this.getW4());
            }
            case COUNTERCLOCKWISE_90:
                return newInstance(this.getZ4(), this.getY4(), -this.getX4(), this.getW4());
        }
    }

    default E withY4(int y) {
        return newInstance(this.getX4(), y, this.getZ4(), this.getW4());
    }
    default E withW4(int w) {
        return newInstance(this.getX4(), this.getY4(), this.getZ4(), w);
    }

    default BlockPos4<?, ?> toImmutable4() {
        return (BlockPos4<?, ?>)(Object) toImmutable();
    }

    default Mutable4 mutableCopy4() {
        return Mutable4.newMutable4(this.getX4(), this.getY4(), this.getZ4(), this.getW4());
    }

    // the following ensure that BlockPos methods are exposed
    // inherited from BlockPos
    default Vec3d toCenterPos() {
        return asBlockPos().toCenterPos();
    }
    // inherited from BlockPos
    default BlockPos rotate(BlockRotation rotation) {
        return asBlockPos().rotate(rotation);
    }
    // inherited from BlockPos
    default BlockPos withY(int y) {
        return asBlockPos().withY(y);
    }
    // inherited from BlockPos
    default BlockPos toImmutable() {
        return asBlockPos().toImmutable();
    }
    // inherited from BlockPos
    default BlockPos.Mutable mutableCopy() {
        return asBlockPos().mutableCopy();
    }

    /** @see BlockPos#method_73158(int, int, int, int, int, int, Vec3d)  */
    static Iterable<BlockPos> method_73158_4(int x0, int y0, int z0, int w0, int x1, int y1, int z1, int w1, Vec4d direction) {
        int minX = Math.min(x0, x1);
        int minY = Math.min(y0, y1);
        int minZ = Math.min(z0, z1);
        int minW = Math.min(w0, w1);

        int maxX = Math.max(x0, x1);
        int maxY = Math.max(y0, y1);
        int maxZ = Math.max(z0, z1);
        int maxW = Math.max(w0, w1);

        int dx = maxX - minX;
        int dy = maxY - minY;
        int dz = maxZ - minZ;
        int dw = maxW - minW;

        final int startX = direction.x4 >= 0 ? minX : maxX;
        final int startY = direction.y  >= 0 ? minY : maxY;
        final int startZ = direction.z  >= 0 ? minZ : maxZ;
        final int startW = direction.w  >= 0 ? minW : maxW;

        ImmutableList<Direction.Axis> axesList = Direction.method_73163(direction);
        Direction4.Axis4 axisY  = Direction4.Axis4.asAxis4(axesList.get(0));
        Direction4.Axis4 axisH0 = Direction4.Axis4.asAxis4(axesList.get(1));
        Direction4.Axis4 axisH1 = Direction4.Axis4.asAxis4(axesList.get(2));
        Direction4.Axis4 axisH2 = Direction4.Axis4.asAxis4(axesList.get(3));

        final Direction4 directionY  = direction.getComponentAlongAxis(axisY ) >= 0 ? axisY .getPositiveDirection4() : axisY .getNegativeDirection4();
        final Direction4 directionH0 = direction.getComponentAlongAxis(axisH0) >= 0 ? axisH0.getPositiveDirection4() : axisH0.getNegativeDirection4();
        final Direction4 directionH1 = direction.getComponentAlongAxis(axisH1) >= 0 ? axisH1.getPositiveDirection4() : axisH1.getNegativeDirection4();
        final Direction4 directionH2 = direction.getComponentAlongAxis(axisH2) >= 0 ? axisH2.getPositiveDirection4() : axisH2.getNegativeDirection4();

        final int dAxisY  = axisY .choose(dx, dy, dz, dw);
        final int dAxisH0 = axisH0.choose(dx, dy, dz, dw);
        final int dAxisH1 = axisH1.choose(dx, dy, dz, dw);
        final int dAxisH2 = axisH2.choose(dx, dy, dz, dw);

        return () -> new AbstractIterator<BlockPos>(){
            private final BlockPos4.Mutable4 mutableBlockPos4 = BlockPos4.Mutable4.newMutable4();

            private int posY;
            private int posH0;
            private int posH1;
            private int posH2;

            private boolean shouldStop;

            private final int axisY_dx = directionY.getOffsetX4();
            private final int axisY_dy = directionY.getOffsetY4();
            private final int axisY_dz = directionY.getOffsetZ4();
            private final int axisY_dw = directionY.getOffsetW4();

            private final int axisH0_dx = directionH0.getOffsetX4();
            private final int axisH0_dy = directionH0.getOffsetY4();
            private final int axisH0_dz = directionH0.getOffsetZ4();
            private final int axisH0_dw = directionH0.getOffsetW4();

            private final int axisH1_dx = directionH1.getOffsetX4();
            private final int axisH1_dy = directionH1.getOffsetY4();
            private final int axisH1_dz = directionH1.getOffsetZ4();
            private final int axisH1_dw = directionH1.getOffsetW4();

            private final int axisH2_dx = directionH2.getOffsetX4();
            private final int axisH2_dy = directionH2.getOffsetY4();
            private final int axisH2_dz = directionH2.getOffsetZ4();
            private final int axisH2_dw = directionH2.getOffsetW4();

            @Override
            protected BlockPos computeNext() {
                if (this.shouldStop) {
                    return this.endOfData();
                }
                this.mutableBlockPos4.set4(
                    startX +  this.axisY_dx * this.posY  +  this.axisH0_dx * this.posH0  +  this.axisH1_dx * this.posH1 +  this.axisH2_dx * this.posH2,
                    startY +  this.axisY_dy * this.posY  +  this.axisH0_dy * this.posH0  +  this.axisH1_dy * this.posH1 +  this.axisH2_dy * this.posH2,
                    startZ +  this.axisY_dz * this.posY  +  this.axisH0_dz * this.posH0  +  this.axisH1_dz * this.posH1 +  this.axisH2_dz * this.posH2,
                    startW +  this.axisY_dw * this.posY  +  this.axisH0_dw * this.posH0  +  this.axisH1_dw * this.posH1 +  this.axisH2_dw * this.posH2
                );
                if (this.posH2 < dAxisH2) {
                    ++this.posH2;
                }
                else if (this.posH1 < dAxisH1) {
                    ++this.posH1;
                    this.posH2 = 0;
                }
                else if (this.posH0 < dAxisH0) {
                    ++this.posH0;
                    this.posH1 = 0;
                    this.posH2 = 0;
                }
                else if (this.posY < dAxisY) {
                    ++this.posY;
                    this.posH0 = 0;
                    this.posH1 = 0;
                    this.posH2 = 0;
                }
                else {
                    this.shouldStop = true;
                }
                return this.mutableBlockPos4.asBlockPos();
            }
        };
    }

    interface BlockPos4Impl extends BlockPos4<BlockPos4Impl, BlockPos> {
        // implemented in mixin
    }

    interface Mutable4 extends BlockPos4<BlockPos4Impl, BlockPos>{
        static Mutable4 newMutable4() {
            return asMutable4(new BlockPos.Mutable());
        }

        static Mutable4 newMutable4(int x, int y, int z, int w) {
            return asMutable4(new BlockPos.Mutable(x, y, z)).setW4(w);
        }

        static Mutable4 newMutable4(double x, double y, double z, double w) {
            return asMutable4(new BlockPos.Mutable(x, y, z)).setW4(MathHelper.floor(w));
        }

        static Mutable4 asMutable4(BlockPos.Mutable mutable) {
            return (Mutable4)(Object) mutable;
        }

        default BlockPos.Mutable asBlockPosMutable() {
            return (BlockPos.Mutable)(Object) this;
        }

        BlockPos.Mutable setW(int w);

        Mutable4 setX4(int x);

        Mutable4 setY4(int y);

        Mutable4 setZ4(int z);

        Mutable4 setW4(int w);

        Mutable4 set4(int x, int y, int z, int w);

        Mutable4 set4(double x, double y, double z, double w);

        Mutable4 set4(Vec4i<?, ?> pos);
        // inherited from BlockPos.Mutable //TODO: wtf does this even do?
        //BlockPos.Mutable set4(AxisCycleDirection axis, int x, int y, int z);

        Mutable4 set4(Vec4i<?, ?> pos, Direction4 direction);

        Mutable4 set4(Vec4i<?, ?> pos, int x, int y, int z, int w);

        Mutable4 set4(Vec4i<?, ?> vec1, Vec4i<?, ?> vec2);

        Mutable4 move4(Direction4 direction);

        Mutable4 move4(Direction4 direction, int distance);

        Mutable4 move4(int dx, int dy, int dz, int dw);

        Mutable4 move4(Vec4i<?, ?> vec);

        Mutable4 clamp4(Direction4.Axis4 axis, int min, int max);

        // the following ensure that BlockPos.Mutable methods are exposed
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(int x, int y, int z) {
            return asBlockPosMutable().set(x, y, z);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(double x, double y, double z) {
            return asBlockPosMutable().set(x, y, z);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(Vec3i pos) {
            return asBlockPosMutable().set(pos);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(long pos) {
            return asBlockPosMutable().set(pos);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(AxisCycleDirection axis, int x, int y, int z) {
            return asBlockPosMutable().set(axis, x, y, z);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(Vec3i pos, Direction direction) {
            return asBlockPosMutable().set(pos, direction);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(Vec3i pos, int x, int y, int z) {
            return asBlockPosMutable().set(pos, x, y, z);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable set(Vec3i vec1, Vec3i vec2) {
            return asBlockPosMutable().set(vec1, vec2);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable move(Direction direction) {
            return asBlockPosMutable().move(direction);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable move(Direction direction, int distance) {
            return asBlockPosMutable().move(direction, distance);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable move(int dx, int dy, int dz) {
            return asBlockPosMutable().move(dx, dy, dz);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable move(Vec3i vec) {
            return asBlockPosMutable().move(vec);
        }
        // inherited from BlockPos.Mutable
        default BlockPos.Mutable clamp(Direction.Axis axis, int min, int max) {
            return asBlockPosMutable().clamp(axis, min, max);
        }
    }
}