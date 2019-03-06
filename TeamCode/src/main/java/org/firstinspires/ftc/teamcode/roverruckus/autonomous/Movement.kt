package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.roverruckus.utils.Motors.*
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.toInches
import kotlin.math.sqrt

fun Robot.land() {
    /* The hook starts down (near the robot).
    *  Make the lifting move up so the robot goes down.
    *  3.7 (gearbox ration on motor) * 28 (encoder counts per revolution) * rotations
    *  Through experimenting we found out that 70 is the perfect number of rotations on the
    *  lifting motor.
    */
    setMotorMode(Lift, DcMotor.RunMode.STOP_AND_RESET_ENCODER)

    motors[Lift]?.targetPosition = (28 * 3.7 * 70).toInt()

    motors[Lift]?.power = -0.95
    motors[Lift]?.mode = DcMotor.RunMode.RUN_TO_POSITION

    while (motors[Lift]?.isBusy!! && opModeIsActive) { writeMotorsTelemetry() }

    motors[Lift]?.power = 0.0
    motors[Lift]?.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
}

fun Robot.moveByDistance(centimeters: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    val target = centimeters.toInches() / (Robot.WHEEL_CIRCUMFERENCE * sqrt(2.0)) * Robot.MOVEMENT_MOTOR_TICK_COUNT

    setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, LF, RF, LB, RB)

    setMotorsTargetPos(target, LF, RF, LB, RB)
    setMotorsPower(power, RF, RB)
    setMotorsPower(-power, LF, LB)
    setMotorsMode(DcMotor.RunMode.RUN_TO_POSITION, LF, RF, LB, RB)

    while (areMotorsBusy(LF, RF, LB, RB)) {
        writeMotorsTelemetry()
    }

    setMotorsPower(0.0, LF, RF, LB, RB)
    setMotorsMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER, LF, RF, LB, RB)
}

fun Robot.strafeByDistance(centimeters: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    val target = centimeters / (Robot.WHEEL_CIRCUMFERENCE * sqrt(2.0)) * Robot.MOVEMENT_MOTOR_TICK_COUNT

    setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER, LF, RF, LB, RB)

    setMotorsTargetPos(target, LF, RB)
    setMotorsTargetPos(-target, RF, LB)
    setMotorsPower(power, RF, RB)
    setMotorsPower(-power, LF, LB)

    setMotorsMode(DcMotor.RunMode.RUN_TO_POSITION, LF, RF, LB, RB)

    while (areMotorsBusy(LF, RF, LB, RB)) {  }

    setMotorsPower(0.0, LF, RF, LB, RB)
    setMotorsMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER, LF, RF, LB, RB)
}

fun Robot.rotate() {

}