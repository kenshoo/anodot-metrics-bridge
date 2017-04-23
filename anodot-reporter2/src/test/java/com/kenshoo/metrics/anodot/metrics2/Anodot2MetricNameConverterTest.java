package com.kenshoo.metrics.anodot.metrics2;

import com.kenshoo.metrics.anodot.DefaultAnodotGlobalProperties;
import com.yammer.metrics.core.spec.MetricName;
import org.junit.Test;

import static com.kenshoo.metrics.anodot.AnodotProperties.*;
import static com.kenshoo.metrics.anodot.DefaultAnodotGlobalProperties.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by tzachz on 2/21/17
 */
public class Anodot2MetricNameConverterTest {

    private final static String VERSION = "V33";
    private final static String BUILD = "1234";

    private Anodot2MetricNameConverter nameConverter = new Anodot2MetricNameConverter(new DefaultAnodotGlobalProperties(VERSION, BUILD));

    @Test
    public void commonCaseParsedProperly() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("com.kenshoo.MyClass.metricNameWith_Underscore.subname");
        assertProperty(anodotName, CLASS_PROPERTY, "MyClass");
        assertProperty(anodotName, PACKAGE_PROPERTY, "com_kenshoo");
        assertProperty(anodotName, WHAT_PROPERTY, "metricNameWith_Underscore_subname");
        assertCommonProperties(anodotName);
    }

    @Test
    public void noUpperCasePartsMeanNoPackageAndClass() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("com.kenshoo.package.metric.subname");
        assertProperty(anodotName, CLASS_PROPERTY, null);
        assertProperty(anodotName, PACKAGE_PROPERTY, null);
        assertProperty(anodotName, WHAT_PROPERTY, "com_kenshoo_package_metric_subname");
        assertCommonProperties(anodotName);
    }

    @Test
    public void uppercasePackageNameMistakenForClassWhatCanYouDo() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("com.kenshoo.BadlyNamedPackage.MyClass.metricName.subname");
        assertProperty(anodotName, CLASS_PROPERTY, "BadlyNamedPackage");
        assertProperty(anodotName, PACKAGE_PROPERTY, "com_kenshoo");
        assertProperty(anodotName, WHAT_PROPERTY, "MyClass_metricName_subname");
        assertCommonProperties(anodotName);
    }

    @Test
    public void noPackageMetricNameFallsBackToUseEntireNameAsWhat() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("MyClass.metricName.subname");
        assertProperty(anodotName, CLASS_PROPERTY, null);
        assertProperty(anodotName, PACKAGE_PROPERTY, null);
        assertProperty(anodotName, WHAT_PROPERTY, "MyClass_metricName_subname");
        assertCommonProperties(anodotName);
    }

    @Test
    public void digitInPackageNameAllowed() throws Exception {
        final MetricName anodotName = nameConverter.toAnodot2Name("org.apache.log4j.Appender.warn");
        assertProperty(anodotName, CLASS_PROPERTY, "Appender");
        assertProperty(anodotName, PACKAGE_PROPERTY, "org_apache_log4j");
        assertProperty(anodotName, WHAT_PROPERTY, "warn");
        assertCommonProperties(anodotName);
    }

    private void assertProperty(MetricName anodotName, String name, String value) {
        if (value == null) {
            assertThat(anodotName.getProperty(name), nullValue());
        } else {
            assertThat(anodotName.getProperty(name).getValue(), is(value));
        }
    }

    private void assertCommonProperties(MetricName anodotName) {
        assertProperty(anodotName, VERSION_PROPERTY, VERSION);
        assertProperty(anodotName, BUILD_PROPERTY, "1234");
        assertThat(anodotName.getProperty(SERVER_PROPERTY), notNullValue());
    }

}