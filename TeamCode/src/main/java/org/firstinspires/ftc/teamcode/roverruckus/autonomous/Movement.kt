package org.firstinspires.ftc.teamcode.roverruckus.autonomous

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.roverruckus.utils.Motors
import org.firstinspires.ftc.teamcode.roverruckus.utils.Robot
import org.firstinspires.ftc.teamcode.roverruckus.utils.toInches
import kotlin.math.PI
import kotlin.math.sqrt

fun Robot.land() {
    /* The hook starts down (near the robot).
    *  Make the lifting move up so the robot goes down.
    *  3.7 (gearbox ration on motor) * 28 (encoder counts per revolution) * rotations
    *  Through experimenting we found out that 72 is the perfect number of rotations on the
    *  lifting motor.
    */
    setMotorMode(Motors.Lift, DcMotor.RunMode.STOP_AND_RESET_ENCODER)

    motors[Motors.Lift]?.targetPosition = (28 * 3.7 * -72).toInt()

    motors[Motors.Lift]?.power = Robot.DEFAULT_MOTOR_POWER
    motors[Motors.Lift]?.mode = DcMotor.RunMode.RUN_TO_POSITION

    while (motors[Motors.Lift]?.isBusy!! && opModeIsActive) { }

    motors[Motors.Lift]?.power = 0.0
    motors[Motors.Lift]?.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
}

fun Robot.moveByDistance(centimeters: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    val target = centimeters / (Robot.WHEEL_CIRCUMFERENCE / 2 * sqrt(2.0)) * Robot.MOVEMENT_MOTOR_TICK_COUNT

}

fun Robot.strafeByDistance(centimeters: Double, power: Double = Robot.DEFAULT_MOTOR_POWER) {
    val target = centimeters / (Robot.WHEEL_CIRCUMFERENCE / 2 * sqrt(2.0)) * Robot.MOVEMENT_MOTOR_TICK_COUNT


}

fun Robot.rotate() {

}