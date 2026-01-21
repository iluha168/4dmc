package com.gmail.inayakitorikhurram.fdmc.mixin.world;

import com.gmail.inayakitorikhurram.fdmc.math.BlockPos4;
import com.gmail.inayakitorikhurram.fdmc.math.Box4;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4d;
import com.gmail.inayakitorikhurram.fdmc.math.Vec4i;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(BlockView.class)
public interface BlockViewMixin {
	/** @see BlockView#method_73110(Vec3d)  */
	@Unique
	private static Vec4i<?, ?> method_73110_4(Vec4d dV) {
		double alignX = Math.abs(Vec4d.X.dotProduct(dV));
		double alignY = Math.abs(Vec4d.Y.dotProduct(dV));
		double alignZ = Math.abs(Vec4d.Z.dotProduct(dV));
		double alignW = Math.abs(Vec4d.W.dotProduct(dV));
		int signX = dV.x4>= 0 ? 1 : -1;
		int signY = dV.y >= 0 ? 1 : -1;
		int signZ = dV.z >= 0 ? 1 : -1;
		int signW = dV.w >= 0 ? 1 : -1;
		if (alignX <= alignY && alignX <= alignZ && alignX <= alignW) {
			return Vec4i.newVec4i(-signX, -signZ, signY,  signW);
		}
		if (alignY <= alignZ && alignY <= alignW) {
			return Vec4i.newVec4i(signZ, -signY, -signX,  signW);
		}
		if (alignZ <= alignW) {
			return Vec4i.newVec4i(-signY, signX, -signZ,  signW);
		}
			return Vec4i.newVec4i(-signY, signX, signZ, -signW);
	}

	@WrapMethod(method = "collectCollisionsBetween(Lit/unimi/dsi/fastutil/longs/LongSet;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/BlockView$CollisionVisitor;)I")
	private static int collectCollisionsInside(LongSet visited, Vec3d dV3, Box box3, BlockView.CollisionVisitor visitor, Operation<Integer> original) {
		if (!(box3 instanceof Box4 box)) return original.call(visited, dV3, box3, visitor);
		Vec4d dV = Vec4d.of(dV3);

		double lenX = box.getLengthX4();
		double lenY = box.getLengthY();
		double lenZ = box.getLengthZ();
		double lenW = box.getLengthW();

		Vec4i<?, ?> signsOfSmth = method_73110_4(dV);
		Vec4d boxCenter = box.getCenter();
		Vec4d posTo = new Vec4d(
			boxCenter.x4+lenX * 0.5 * (double)signsOfSmth.getX4(),
			boxCenter.y + lenY * 0.5 * (double)signsOfSmth.getY4(),
			boxCenter.z + lenZ * 0.5 * (double)signsOfSmth.getZ4(),
			boxCenter.w + lenW * 0.5 * (double)signsOfSmth.getW4()
		);
		Vec4d posFrom = posTo.subtract(dV);

		int posFromXIntPart = MathHelper.floor(posFrom.x4);
		int posFromYIntPart = MathHelper.floor(posFrom.y);
		int posFromZIntPart = MathHelper.floor(posFrom.z);
		int posFromWIntPart = MathHelper.floor(posFrom.w);

		int dxSign = MathHelper.sign(dV.x4);
		int dySign = MathHelper.sign(dV.y);
		int dzSign = MathHelper.sign(dV.z);
		int dwSign = MathHelper.sign(dV.w);

		double dxAbs = dxSign == 0 ? Double.MAX_VALUE : (double)dxSign / dV.x4;
		double dyAbs = dySign == 0 ? Double.MAX_VALUE : (double)dySign / dV.y;
		double dzAbs = dzSign == 0 ? Double.MAX_VALUE : (double)dzSign / dV.z;
		double dwAbs = dwSign == 0 ? Double.MAX_VALUE : (double)dwSign / dV.w;

		double posFromXFractionalPart = dxAbs * (dxSign > 0 ? 1 - MathHelper.fractionalPart(posFrom.x4) : MathHelper.fractionalPart(posFrom.x4));
		double posFromYFractionalPart = dyAbs * (dySign > 0 ? 1 - MathHelper.fractionalPart(posFrom.y) : MathHelper.fractionalPart(posFrom.y));
		double posFromZFractionalPart = dzAbs * (dzSign > 0 ? 1 - MathHelper.fractionalPart(posFrom.z) : MathHelper.fractionalPart(posFrom.z));
		double posFromWFractionalPart = dwAbs * (dwSign > 0 ? 1 - MathHelper.fractionalPart(posFrom.w) : MathHelper.fractionalPart(posFrom.w));

		int s = 0;
		while (posFromXFractionalPart <= 1 || posFromYFractionalPart <= 1 || posFromZFractionalPart <= 1 || posFromWFractionalPart <= 1) {
			if (posFromXFractionalPart < posFromYFractionalPart && posFromXFractionalPart < posFromZFractionalPart && posFromXFractionalPart < posFromWFractionalPart) {
				posFromXIntPart += dxSign;
				posFromXFractionalPart += dxAbs;
			} else if (posFromYFractionalPart < posFromZFractionalPart && posFromYFractionalPart < posFromWFractionalPart) {
				posFromYIntPart += dxSign;
				posFromYFractionalPart += dxAbs;
			} else if (posFromZFractionalPart < posFromWFractionalPart) {
				posFromZIntPart += dxSign;
				posFromZFractionalPart += dxAbs;
			} else {
				posFromWIntPart += dxSign;
				posFromWFractionalPart += dxAbs;
			}

			Optional<Vec4d> optional = Box4.raycast(posFromXIntPart, posFromYIntPart, posFromZIntPart, posFromWIntPart, posFromXIntPart + 1, posFromYIntPart + 1, posFromZIntPart + 1, posFromWIntPart + 1, posFrom, posTo);
			if (optional.isEmpty()) continue;
			Vec4d rayCastResult = optional.get();

			++s;

			for (BlockPos blockPos : BlockPos4.method_73158_4(
				posFromXIntPart, posFromYIntPart, posFromZIntPart, posFromWIntPart,
				MathHelper.floor(MathHelper.clamp(rayCastResult.x4,(double)posFromXIntPart + MathHelper.EPSILON, (double)posFromXIntPart + 1 - MathHelper.EPSILON) - lenX * (double)signsOfSmth.getX4()),
				MathHelper.floor(MathHelper.clamp(rayCastResult.y, (double)posFromYIntPart + MathHelper.EPSILON, (double)posFromYIntPart + 1 - MathHelper.EPSILON) - lenY * (double)signsOfSmth.getY4()),
				MathHelper.floor(MathHelper.clamp(rayCastResult.z, (double)posFromZIntPart + MathHelper.EPSILON, (double)posFromZIntPart + 1 - MathHelper.EPSILON) - lenZ * (double)signsOfSmth.getZ4()),
				MathHelper.floor(MathHelper.clamp(rayCastResult.w, (double)posFromWIntPart + MathHelper.EPSILON, (double)posFromWIntPart + 1 - MathHelper.EPSILON) - lenW * (double)signsOfSmth.getW4()),
				dV
			)) {
				if (!visited.add(blockPos.asLong()) || visitor.visit(blockPos, s)) continue;
				return -1;
			}
		}
		return s;
	}
}
