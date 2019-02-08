package org.firstinspires.ftc.teamcode.roverruckus.utils

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector

class Robot(val opMode: OpMode) {
    val telemetry
        get() = opMode.telemetry

    val hardwareMap
        get() = opMode.hardwareMap

    val opModeIsActive
        get() = (opMode as LinearOpMode).opModeIsActive()

    lateinit var leftMotor: DcMotor
    lateinit var rightMotor: DcMotor

    lateinit var leftArm: DcMotor
    lateinit var rightArm: DcMotor

    lateinit var hookServo: Servo
    lateinit var lockServo: Servo

    lateinit var markerServo: Servo

    lateinit var imu: BNO055IMU

    lateinit var angles: Orientation
    lateinit var gravity: Acceleration

    var lastAngles: Orientation = Orientation()
    var globalAngle: Double = 0.0

    var vuforia: VuforiaLocalizer? = null
    var tfod: TFObjectDetector? = null

    fun init(){
        leftMotor = hardwareMap.get(DcMotor::class.java, "l_mov")
        rightMotor = hardwareMap.get(DcMotor::class.java, "r_mov")

        leftMotor.direction = DcMotorSimple.Direction.REVERSE
        rightMotor.direction = DcMotorSimple.Direction.FORWARD
        leftMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rightMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        leftMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        rightMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        leftArm = hardwareMap.get(DcMotor::class.java, "l_arm")
        rightArm = hardwareMap.get(DcMotor::class.java, "r_arm")

        leftArm.direction = DcMotorSimple.Direction.FORWARD
        rightArm.direction = DcMotorSimple.Direction.REVERSE
        leftArm.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        rightArm.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        hookServo = hardwareMap.get(Servo::class.java, "hook")
        lockServo = hardwareMap.get(Servo::class.java, "lock")

        markerServo = hardwareMap.get(Servo::class.java, "marker")

        val imuParams = BNO055IMU.Parameters()
        imuParams.angleUnit = BNO055IMU.AngleUnit.DEGREES
        imuParams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        imuParams.calibrationDataFile = "BNO055IMUCalibration.json" // see the calibration sample opmode
        imuParams.loggingEnabled = true
        imuParams.loggingTag = "IMU"
        imuParams.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()

        imu = hardwareMap.get(BNO055IMU::class.java, "imu")
        imu.initialize(imuParams)
    }

    fun setMotorsMode(mode: DcMotor.RunMode, vararg motors: DcMotor) {
        for (motor in motors) {
            motor.mode = mode
        }
    }

    fun setMotorsPower(power: Double, vararg motors: DcMotor) {
        for (motor in motors) {
            motor.power = power
        }
    }

    companion object {
        const val DEFAULT_MOTOR_SPEED = 0.5
        const val MOVEMENT_MOTOR_TICK_COUNT = 1440
        const val WHEEL_DIAMETER = 4.0 // inches
        const val RATIO = 3.0
        const val WHEEL_CIRCUMFERENCE = Math.PI * 4.0// inches

        val TFOD_MODEL_ASSET = "RoverRuckus.tflite"
        val LABEL_GOLD_MINERAL = "Gold Mineral"
        val LABEL_SILVER_MINERAL = "Silver Mineral"

        val VUFORIA_KEY = "AWo7bzb/////AAABmcbdWZ79Y049lfMcsRS8waNYev8AbC1EwUWqhJnr1poItrv7+etQ1bwW4BiQpg151evO66Pzt3L2LvfbBgzn4aQ3QzVBXYQBjqMScjg/gQEj0g3ldi/0ENHSKwnT48YDxtQQb5/twpwjew9wlaSkZuZ8KtZGwOZHh7vhV0xQmjh1akuPF0zmKvCn5HPnd/O9YxXR5Ef7eyQ+r15XMT7Vd7kG/PUbpCvkexwsRZ4BKGv+oV1ZWOqrYrP5WKbpzHmEOl8RggfJKD707G2Q61vTUW+MEksQwrydbwTCqzTxDUTWdOlgzG9JfGjS+jUdQ3CAN+EETNZDOQs8fIxn3Q+Bdmi823AJLEU3GDhptc7KHcjo"
    }
}
