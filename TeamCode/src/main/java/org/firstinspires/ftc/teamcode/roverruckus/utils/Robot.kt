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
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector

enum class Motors {
    LeftFront,
    RightFront,
    LeftBack,
    RightBack,
    Lift
}

class Robot(val opMode: OpMode) {
    lateinit var motors: HashMap<Motors, DcMotor>

    val telemetry
        get() = opMode.telemetry

    val hardwareMap
        get() = opMode.hardwareMap

    val opModeIsActive
        get() = (opMode as LinearOpMode).opModeIsActive()

    lateinit var imu: BNO055IMU

    lateinit var angles: Orientation
    lateinit var gravity: Acceleration

    var lastAngles: Orientation = Orientation()
    var globalAngle: Double = 0.0

    var vuforia: VuforiaRoverRuckus? = null
    var vuforiaLocalizer: VuforiaLocalizer? = null
    var tfod: TFObjectDetector? = null

    fun init() {
        motors = hashMapOf(
                Motors.LeftFront to hardwareMap.get(DcMotor::class.java, "lf"),
                Motors.RightFront to hardwareMap.get(DcMotor::class.java, "rf"),
                Motors.LeftBack to hardwareMap.get(DcMotor::class.java, "lb"),
                Motors.RightBack to hardwareMap.get(DcMotor::class.java, "rb"),
                Motors.Lift to hardwareMap.get(DcMotor::class.java, "lift")
        )

        motors[Motors.Lift]?.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        motors[Motors.LeftFront]?.direction = DcMotorSimple.Direction.REVERSE
        motors[Motors.RightFront]?.direction = DcMotorSimple.Direction.FORWARD
        motors[Motors.LeftBack]?.direction = DcMotorSimple.Direction.REVERSE
        motors[Motors.RightBack]?.direction = DcMotorSimple.Direction.FORWARD

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

    fun setMotorMode(motor: Motors, mode: DcMotor.RunMode) {
        this.motors[motor]?.mode = mode
    }

    fun setMotorsMode(mode: DcMotor.RunMode, vararg motors: Motors) {
        for (motor in motors) {
            setMotorMode(motor, mode)
        }
    }

    fun setMotorPower(motor: Motors, power: Double) {
        this.motors[motor]?.power = power
    }

    fun setMotorsPower(power: Double, vararg motors: Motors) {
        for (motor in motors) {
            setMotorPower(motor, power)
        }
    }

    companion object {
        const val DEFAULT_MOTOR_POWER = 0.5
        const val MOVEMENT_MOTOR_TICK_COUNT = 1920
        const val WHEEL_DIAMETER = 4.0 // inches
        const val RATIO = 3.0
        const val WHEEL_CIRCUMFERENCE = Math.PI * 4.0// inches

        val TFOD_MODEL_ASSET = "RoverRuckus.tflite"
        val LABEL_GOLD_MINERAL = "Gold Mineral"
        val LABEL_SILVER_MINERAL = "Silver Mineral"

        val VUFORIA_KEY = "AWo7bzb/////AAABmcbdWZ79Y049lfMcsRS8waNYev8AbC1EwUWqhJnr1poItrv7+etQ1bwW4BiQpg151evO66Pzt3L2LvfbBgzn4aQ3QzVBXYQBjqMScjg/gQEj0g3ldi/0ENHSKwnT48YDxtQQb5/twpwjew9wlaSkZuZ8KtZGwOZHh7vhV0xQmjh1akuPF0zmKvCn5HPnd/O9YxXR5Ef7eyQ+r15XMT7Vd7kG/PUbpCvkexwsRZ4BKGv+oV1ZWOqrYrP5WKbpzHmEOl8RggfJKD707G2Q61vTUW+MEksQwrydbwTCqzTxDUTWdOlgzG9JfGjS+jUdQ3CAN+EETNZDOQs8fIxn3Q+Bdmi823AJLEU3GDhptc7KHcjo"
    }
}
