package us.wmwm.onyx.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.kelly.controller.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.core.SingleOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import us.wmwm.onyx.common.ControllerSetting
import us.wmwm.onyx.db.BluetoothDvc
import us.wmwm.onyx.domain.*
import us.wmwm.onyx.preset.Preset
import us.wmwm.onyx.settings.ControllerSettingChange
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.TimeUnit

class BluetoothManager(val adapter: BluetoothAdapter) {

    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null
    private var device: BluetoothDevice? = null

    private val _connected = BehaviorSubject.create<Pair<BluetoothDevice, BluetoothConnection>>()

    private val _data = BehaviorSubject.create<FlashData>()

    private val _monitor = BehaviorSubject.create<MonitorData>()

    private val _percentage = PublishSubject.create<Int>()

    private val _writeCompleted = BehaviorSubject.create<Pair<Boolean, IntArray>>()

    private var readerDis: Disposable? = null

    private val deviceKelly = ACAduserEnglishDeviceKerry.getInstance()

    var monitorThread: ACAduserEnglishDeviceKerry.MonitorThreader? = null

    val DataValue = IntArray(512)

    val connected: Observable<Pair<BluetoothDevice, BluetoothConnection>>
        get() {
            return _connected
        }

    val percentage:Observable<Int> = _percentage

    val data: Observable<FlashData> = _data
    val monitor: Observable<MonitorData> = _monitor
    val writeCompleted: Observable<Pair<Boolean, IntArray>> = _writeCompleted

    val isConnected: Boolean
        get() {
            return socket?.isConnected == true
        }

    fun connect(device: BluetoothDvc) {
        this.device = adapter.getRemoteDevice(device.device)
        connect(this.device!!)
    }

    fun connect(device: BluetoothDevice) {
        this.device = device
        if (device.name == "UNKNOWN" || device.name == null) {
            return
        }
        val bonded = adapter.bondedDevices

        Single.just(1)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe { t1, t2 ->
                if (!bonded.contains(device)) {
                    _connected.onNext(device to BluetoothConnection.BONDING)
                    device.createBond()
                    return@subscribe
                }
                _connected.onNext(device to BluetoothConnection.CONNECTING)
                val socket = device.createRfcommSocketToServiceRecord(MY_UUID)
                this.socket = socket
                try {
                    socket.connect()
                    input = socket.inputStream
                    output = socket.outputStream
                    deviceKelly.setSocket(socket)
                } catch (e: Exception) {
                    socket.close()
                }
                _connected.onNext(device to socket.isConnected.btConnection)
            }

    }

    fun sendCommand(command: Command) {
        Single.create(SendCommandOnSubscribe(command, input ?: return, output ?: return, this))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .filter {
                isConnected
            }
            .subscribe({
                //read
            }, {
                it.printStackTrace()
            })
    }

    fun onDisconnect(dev: BluetoothDevice) {
        socket?.close()
        input?.close()
        output?.close()
        _connected.onNext(dev to BluetoothConnection.DISCONNECTED)
    }

    fun readDataFromKelly(): IntArray {
        println("readDataFromKelly")
        var Read_ok = 1
//        if (this.DEBUG) {
//            this.deviceKelly.resetLogBuffer()
//        }
        if (true) {
            if (isConnected) {
                var j: Int
                var i: Int
                try {
                    this.deviceKelly.ETS_TX_CMD = (-15).toByte()
                    this.deviceKelly.ETS_TX_BYTES = 0.toByte()
                    j = 0
                    loop0@ while (j < 2) {
                        this.deviceKelly.sendcmd()
                        i = 0
                        while (i < 5) {
                            if (this.deviceKelly.readcmd() == 1) {
                                break@loop0
                            }
                            i++
                        }
                        j++
                    }
                } catch (e: java.lang.Exception) {
                    throw RuntimeException("Open Flash Internal Error!")
                }
                var temp_return = 0
                i = 0
                while (i < 32) {
                    try {
                        this.deviceKelly.ETS_TX_CMD = (-14).toByte()
                        this.deviceKelly.ETS_TX_BYTES = 3.toByte()
                        this.deviceKelly.ETS_TX_DATA[0] = (i * 16).toByte()
                        this.deviceKelly.ETS_TX_DATA[1] = 16.toByte()
                        this.deviceKelly.ETS_TX_DATA[2] = (i * 16 shr 8).toByte()
                        j = 0
                        while (j < 2) {
                            this.deviceKelly.sendcmd()
                            for (k in 0..4) {
                                temp_return = this.deviceKelly.readcmd()
                                if (temp_return == 1) {
                                    break
                                }
                            }
                            j++
                        }
                        if (temp_return != 1) {
                            throw RuntimeException("Data Read Error,Maybe the controller no power-on or wire-disconnect!");
                        }
                        if (temp_return == 1) {
                            Read_ok = 1
                        }
                        if (Read_ok == 1) {
                            j = 0
                            while (j < 16) {
                                DataValue[i * 16 + j] = this.deviceKelly.ETS_RX_DATA[j].toInt()
                                if (DataValue[i * 16 + j] < 0) {
                                    val iArr = DataValue
                                    val i2 = i * 16 + j
                                    iArr[i2] = iArr[i2] + 256
                                }
                                j++
                            }
                        }
                        i++
                    } catch (e2: java.lang.Exception) {
                    }
                }
            } else {
                throw RuntimeException("Device is not opened!Please open device first!")
            }
        }
        try {
            //this.formGrid.removeAllViews()
            //this.headView.removeAllViews()
            //ShowKellypageCalibration()
        } catch (e3: java.lang.Exception) {
            throw RuntimeException()
            //showLogInfo("Interface Show Error!Please Try Again!")
        }
//        if (this.DEBUG) {
//            showTextView(this.ceshi, this.deviceKelly.getLogBufferInfo())
//        }
//        this.sendBtn.setEnabled(true)
//        this.sendBtn.setTextColor(ViewCompat.MEASURED_STATE_MASK)
        return DataValue
    }

    fun startReadThread() {
        deviceKelly.readThread()
    }

    fun startMonitorThread() {
        println("starting monitor thread")
        monitorThread = deviceKelly.monitorThread(this)
    }

    fun onRead(k: FlashData) {
        this._data.onNext(k)
    }

    fun onMonitor(monitorMap: MutableMap<Int, Int>) {
        val volt = monitorMap[9]!!
        val motor = monitorMap[10]!!
        val controller = monitorMap[11]!!
        val data = MonitorData(
            voltage = volt,
            controllerTemp = controller,
            motorTemp = motor
        )
        _monitor.onNext(data)
    }

    fun pausePulse() {
        monitorThread?.let {
            it.pause()
        }
    }

    fun resumePulse() {
        startMonitorThread()
    }

    fun onOpen() {
        startReadThread()
        startMonitorThread()
    }

    fun disconnect() {
        socket?.close()
        input?.close()
        output?.close()
        device?.let {
            _connected.onNext(
                Pair(
                    it,
                    socket?.isConnected?.btConnection ?: BluetoothConnection.DISCONNECTED
                )
            )
        }

    }

    fun writeDataToKelly(value: List<ControllerSettingChange>) {
        val newFLash = this.DataValue.copyOf()
        val data = _data.value
        println(newFLash)
        value.forEach {
            val row = data.version.ids[it.to.setting.code]!!
            val value = it.to.value.toString()
            if (ACAduserEnglishByteUtil.printStringArrayInt(
                    newFLash,
                    row[0].toInt(),
                    row[1].toInt(),
                    row[2].toInt(),
                    row[3],
                    value
                ) == 0
            ) {
                throw java.lang.RuntimeException()
            }
            val k = ACAduserEnglishByteUtil.printIntArrayString(
                newFLash,
                row[0].toInt(),
                row[1].toInt(),
                row[2].toInt(),
                row[3]
            )
            if (k != value) {
                throw java.lang.RuntimeException("we failed")
            }
        }
        println(newFLash)
        Single.just(1)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { t1, t2 ->
                try {
                    sendDataToKelly(newFLash)
                    _writeCompleted.onNext(true to newFLash)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

    }

    private fun sendDataToKelly(data: IntArray) {
        var j: Int
        var i: Int
        var Write_ok = 1
        var temp_return = 0

        if (isConnected) {
            try {
                deviceKelly.ETS_TX_CMD = (-15).toByte()
                deviceKelly.ETS_TX_BYTES = 0.toByte()
                j = 0
                loop0@ while (j < 2) {
                    deviceKelly.sendcmd()
                    i = 0
                    while (i < 5) {
                        temp_return = deviceKelly.readcmd()
                        if (temp_return == 1) {
                            break@loop0
                        }
                        i++
                    }
                    j++
                }
                _percentage.onNext(1)
                if (temp_return != 1) {
                    throw RuntimeException("Open Flash Failed!")
                }
            } catch (e: java.lang.Exception) {
                throw RuntimeException("Open Flash Internal Error!")
            }
        } else {
            throw RuntimeException("Device is not opened!Please open device first!")
        }


        i = 0
        while (i < 40) {
            println("$i < 40")
            _percentage.onNext(1+i)
            if (i < 39) {
                deviceKelly.ETS_TX_CMD = (-13).toByte()
                deviceKelly.ETS_TX_BYTES = 16.toByte()
                deviceKelly.ETS_TX_DATA[0] = (i * 13).toByte()
                deviceKelly.ETS_TX_DATA[1] = 13.toByte()
                deviceKelly.ETS_TX_DATA[2] = (i * 13 shr 8).toByte()
                j = 0
                while (j < 13) {
                    println("$i $j < 13")
                    deviceKelly.ETS_TX_DATA[j + 3] =
                        data[i * 13 + j].toByte()
                    j++
                }
                j = 0
                while (j < 3) {
                    println("$i < 40 && $j < 3")
                    try {
                        deviceKelly.sendcmd()
                        while (0 < 10) {
                            temp_return = deviceKelly.readcmd()
                            if (temp_return == 1) {
                                break
                            }
                            i++
                        }
                        j++
                    } catch (e2: java.lang.Exception) {
                        throw RuntimeException("Write Error!")
                    }
                }
                if (temp_return != 1) {
                    Write_ok = 0
                }
            } else if (i == 39) {
                try {
                    deviceKelly.ETS_TX_CMD = (-13).toByte()
                    deviceKelly.ETS_TX_BYTES = 8.toByte()
                    deviceKelly.ETS_TX_DATA[0] = (-5).toByte()
                    deviceKelly.ETS_TX_DATA[1] = 5.toByte()
                    deviceKelly.ETS_TX_DATA[2] = 1.toByte()
                    j = 507
                    while (j < 512) {
                        println("$i == 39 $j < 512")
                        deviceKelly.ETS_TX_DATA[j - 507 + 3] =
                            data[j].toByte()
                        j++
                    }
                    j = 0
                    while (j < 3) {
                        println("$i == 39 $j<3")
                        deviceKelly.sendcmd()
                        while (0 < 10) {
                            println("$i == 39 && $j<3 && 0 < 10")
                            temp_return = deviceKelly.readcmd()
                            if (temp_return == 1) {
                                break
                            }
                            i++
                        }
                        j++
                    }
                    if (temp_return != 1) {
                        Write_ok = 0
                    }
                } catch (e3: java.lang.Exception) {
                    throw RuntimeException("Write Error!")
                }
            }
            i++
        }
        deviceKelly.ETS_TX_CMD = (-12).toByte()
        deviceKelly.ETS_TX_BYTES = 0.toByte()
        deviceKelly.sendcmd()
        i = 0
        while (i < 30) {
            _percentage.onNext(70+i)
            println("$i < 30")
            temp_return = deviceKelly.readcmd()
            if (temp_return == 1) {
                break
            }
            i++
        }
        if (temp_return != 1) {
            deviceKelly.closeUsbComDevice()
            throw RuntimeException("Write To Flash Error!")
        } else if (Write_ok == 0) {
            throw RuntimeException("Write Data Error!")
        }
        println("Data Write Completely!")
    }
}

enum class Command {
    OPEN,
    READ_FLASH
}

class SendCommandOnSubscribe(
    val command: Command,
    input: InputStream,
    output: OutputStream,
    val bt: BluetoothManager
) : SingleOnSubscribe<IntArray> {

    val input = LoggableInputStream(input)
    val output = LoggableOutputStream(output)


    object def {
        val commandToLambda =
            mutableMapOf(Command.OPEN to OpenCommandReader, Command.READ_FLASH to FlashReadCommand)
        //val commandToParser = mutableMapOf(Command.READ_FLASH to FlashParser)
    }

    override fun subscribe(emitter: SingleEmitter<IntArray>) {
        val lambda = def.commandToLambda[command] ?: return
        if (emitter.isDisposed) {
            return
        }
        try {
            val data = lambda.invoke(bt, input, output)
            emitter.onSuccess(data)

        } catch (e: java.lang.Exception) {
            if (emitter.isDisposed) {
                return
            }
            emitter.onError(e)
        }
    }

}

class LoggableInputStream(val input: InputStream) : InputStream() {
    override fun read(): Int {
        return input.read()
    }

    override fun read(b: ByteArray): Int {
        val c = input.read(b)
        println("read $c ${b.contentToString()}")
        return c
    }

    override fun available(): Int {
        return input.available()
    }

    override fun close() {
        input.close()
    }

    override fun mark(readlimit: Int) {
        input.mark(readlimit)
    }

    override fun markSupported(): Boolean {
        return input.markSupported()
    }

    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        return input.read(b, off, len)
    }

    override fun reset() {
        input.reset()
    }

    override fun skip(n: Long): Long {
        return input.skip(n)
    }
}

class LoggableOutputStream(val out: OutputStream) : OutputStream() {
    override fun close() {
        out.close()
    }

    override fun flush() {
        out.flush()
    }

    override fun write(p0: Int) {
        out.write(p0)
    }

    override fun write(b: ByteArray) {
        println("writing ${b.size} ${b.contentToString()}")
        out.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        out.write(b, off, len)
    }
}

object OpenCommandReader : (BluetoothManager, InputStream, OutputStream) -> IntArray {
    override fun invoke(bt: BluetoothManager, input: InputStream, out: OutputStream): IntArray {
        val send = ByteArray(3)
        send[0] = ETS.CODE_VERSION
        send[1] = 0
        send[2] = ETS.CODE_VERSION
        out.write(send)
        Thread.sleep(400)
        val buffer = ByteArray(5)
        val read = input.read(buffer)
        if (read == 1) {
            throw OpenError()
        }
        val sub = IntArray(read)
        val map = buffer.map { it.toInt() }.toIntArray()
        map.copyInto(sub, 0, 0, read)
        bt.onOpen()
        return sub
    }
}

object FlashReadCommand : (BluetoothManager, InputStream, OutputStream) -> IntArray {
    override fun invoke(b: BluetoothManager, p1: InputStream, p2: OutputStream): IntArray {
        try {
            val data = b.readDataFromKelly()
            val k = FlashParser(data)
            b.onRead(k)
        } catch (e: java.lang.Exception) {
            println("err")
            throw java.lang.RuntimeException(e)
        }
        println("suc")
        return b.DataValue
    }
}

object FlashParser : (IntArray) -> FlashData {
    override fun invoke(p1: IntArray): FlashData {
        val Temp_asc = ACAduserEnglishByteUtil.printIntArrayString(p1, 0, 2, 7, "a")
        val Soft_Version2 = (p1[16] shl 8) + p1[17]
        val ControlType1 = p1[84]
        val ControlType2 = p1[85]
        val isCAN = ControlType2 != 75 || (ControlType2 == 75 && ControlType1 == 54)
        val Name = Temp_asc.substring(1, 4)
        val NewName = Temp_asc.substring(1, 3)
        val volts = ACAduserEnglishByteUtil.printIntArrayString(p1, 3, 2, 1, "a")
        val flashVersion = when {
            Name == "BLS" || NewName == "LS" || Name == "BSS" -> {
                when {
                    Soft_Version2 >= 265 -> {
                        FlashVersion(
                            softwareVersion = SoftwareVersion.TWO,
                            identifyShowEn = false,
                            calibrationArray = ACAduserEnglishSetting.Calibration_Value_Array_KBLS0109
                        )
                    }
                    Soft_Version2 >= 262 -> {
                        FlashVersion(
                            softwareVersion = SoftwareVersion.TWO,
                            identifyShowEn = false,
                            calibrationArray = ACAduserEnglishSetting.Calibration_Value_Array_KBLS0106
                        )
                    }
                    Soft_Version2 == 261 -> {
                        FlashVersion(
                            softwareVersion = SoftwareVersion.TWO,
                            identifyShowEn = false,
                            calibrationArray = ACAduserEnglishSetting.Calibration_Value_Array_KBLS0105
                        )
                    }
                    Soft_Version2 == 260 -> {
                        FlashVersion(
                            softwareVersion = SoftwareVersion.TWO,
                            identifyShowEn = false,
                            calibrationArray = ACAduserEnglishSetting.Calibration_Value_Array_KBLS0104
                        )
                    }
                    Soft_Version2 >= 258 -> {
                        FlashVersion(
                            softwareVersion = SoftwareVersion.TWO,
                            identifyShowEn = true,
                            calibrationArray = ACAduserEnglishSetting.Calibration_Value_Array_KBLS0102
                        )
                    }
                    Soft_Version2 == 257 -> {
                        FlashVersion(
                            softwareVersion = SoftwareVersion.TWO,
                            identifyShowEn = true,
                            calibrationArray = ACAduserEnglishSetting.Calibration_Value_Array_KBLS0101
                        )
                    }
                    else -> throw UnsupportedOperationException("module not supported")
                }
            }
            (Name != "ACS") && (NewName != "PS") -> {
                when {
                    (Name != "ACI") && (Name == "ACS" || NewName != "AC") -> throw UnsupportedOperationException(
                        "module not supported"
                    )
                    else -> {
                        when {
                            Soft_Version2 >= 259 -> {
                                FlashVersion(
                                    softwareVersion = SoftwareVersion.ONE,
                                    identifyShowEn = false,
                                    calibrationArray = ACAduserEnglishSetting2.Calibration_Value_Array_KACI0103
                                )
                            }
                            Soft_Version2 == 258 -> {
                                FlashVersion(
                                    softwareVersion = SoftwareVersion.ONE,
                                    identifyShowEn = false,
                                    calibrationArray = ACAduserEnglishSetting2.Calibration_Value_Array_KACI0102
                                )
                            }
                            Soft_Version2 == 257 -> {
                                FlashVersion(
                                    softwareVersion = SoftwareVersion.ONE,
                                    identifyShowEn = false,
                                    calibrationArray = ACAduserEnglishSetting2.Calibration_Value_Array_KACI0101
                                )
                            }
                            else -> {
                                throw UnsupportedOperationException("module not supported")
                            }
                        }
                    }
                }
            }
            else -> throw UnsupportedOperationException("module not supported")
        }

        val ids = flashVersion.calibrationArray.indices.map {
            flashVersion.calibrationArray[it][0].toInt() to flashVersion.calibrationArray[it]
        }.toMap()

        val voltage = listOfNotNull(ids[25], ids[27])
            .first().run {
                val voltstr = ACAduserEnglishByteUtil.printIntArrayString(p1, 3, 2, 1, "a")
                when (voltstr) {
                    "80" -> return@run Voltage(min = 18, max = (p1[23] * 256 + p1[24]) * 125 / 100)
                    else -> return@run Voltage(
                        min = ACAduserEnglishByteUtil.getVoltRange(
                            voltstr,
                            0
                        ), max = ACAduserEnglishByteUtil.getVoltRange(voltstr, 1)
                    )
                }
            }

        val currentVolt = ids[9].run {
            val percent = p1[9]
            percent
        }
        val serial = ids[12]!!
        val serialNo = ACAduserEnglishByteUtil.printIntArrayString(
            p1,
            serial[0].toInt(),
            serial[1].toInt(),
            serial[2].toInt(),
            serial[3]
        )
        val settings = Preset.ONYX.presetData.map {
            val row = ids[it.setting.code]!!
            val data = ACAduserEnglishByteUtil.printIntArrayString(
                p1,
                row[0].toInt(),
                row[1].toInt(),
                row[2].toInt(),
                row[3]
            )
            ControllerSetting(
                setting = it.setting,
                value = data.toInt()
            )
        }
        val data = FlashData(
            controllerName = Temp_asc,
            serialNumber = serialNo,
            version = flashVersion.copy(
                ids = ids
            ),
            softwareVersion2 = Soft_Version2,
            name = Name,
            newName = NewName,
            volts = volts.toInt(),
            voltage = voltage,
            currentVolt = currentVolt,
            settings = settings,
            rawData = p1
        )
        return data
    }

}

class ThreadPausedError : Exception()

class OpenError : Error()
class ReadError : Error()

object ETS {
    val CODE_VERSION: Byte = 17
    val ENTRY_IDENTIFY: Byte = 67
    val CHECK_IDENTIFY_STATUS = 68
    val GET_HALL_SEQUENCE = 78
    val GET_PHASE_I_AD = 53
    val GET_PMSM_PARM = 75
    val GET_RESOLVER_INIT_ANGLE: Byte = 77
    val ETS_14: Byte = -14
    val ETS_15: Byte = -15
    val QUIT_IDENTIFY = 66
    val WRITE_PMSM_PARM = 76
}

class CommandPayload(
    val cmd: Byte,
    val byte: Byte,
    val data: ByteArray = ByteArray(0),
    val handler: (InputStream, OutputStream) -> Any
)

enum class BluetoothConnection {
    BONDING, CONNECTING, CONNECTED, CONNECTION_FAILED, DISCONNECTED
}

val Boolean.btConnection: BluetoothConnection
    get() {
        return if (this) {
            BluetoothConnection.CONNECTED
        } else {
            BluetoothConnection.CONNECTION_FAILED
        }
    }