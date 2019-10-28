import groovy.xml.MarkupBuilder
import groovy.io.FileType

def filesDependecies = ""

def pathDependencies = new File("target/dependency")
pathDependencies.eachFileRecurse(FileType.FILES) {
    file ->
        filesDependecies = filesDependecies + ";" + file
}
filesDependecies = filesDependecies.replace("\\", "/")

File dir = new File("asoc");
if (!dir.exists()) dir.mkdirs();
def fileASoCConfig = new FileWriter("appscan-config.xml")
def xmlASoC = new MarkupBuilder(fileASoCConfig)

xmlASoC.mkp.xmlDeclaration(version: "1.0", encoding: "UTF-8", standalone: "no")
xmlASoC.Configuration {
    Targets {
        Target(path: "target/classes") {
            Exclude('*.jar')
            CustomBuildInfo(jdk_path: "agent/JAVA_HOME/jre", src_root: 'src/main/java;' + 'src/main/resources;src/test/java', additional_classpath: filesDependecies + ";" + "target/classes") {}
        }
    }

}
fileASoCConfig.close();
println filesDependecies