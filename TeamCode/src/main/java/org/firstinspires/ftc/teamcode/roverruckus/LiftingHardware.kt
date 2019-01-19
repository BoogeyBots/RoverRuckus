package org.firstinspires.ftc.teamcode.roverruckus

import android.os.PowerManager
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo

class LiftingHardware {
    private lateinit var liftingMotor: DcMotor

    var liftingPower: Double = 0.0
        set(value) {
            if (value != field) {
                field = value
                liftingMotor.power = field
            }
        }

    fun init(hardwareMap: HardwareMap) {
        liftingMotor = hardwareMap.get(DcMotor::class.java, "lifting_motor")
        liftingMotor.direction = DcMotorSimple.Direction.FORWARD
        liftingMotor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }
}