import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  ActivityIndicator,
  Image,
} from 'react-native';
import { useState, useEffect } from 'react';
import { SafeAreaView } from 'react-native-safe-area-context';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import api from '../../services/api';
import { Colors } from '../../constants/colors';

export default function PrescriptionScreen() {
  const [prescriptions, setPrescriptions] = useState<any[]>([]);
  const [remaining, setRemaining] = useState<Record<string, any[]>>({});
  const [loading, setLoading] = useState(true);
  const [expandedQr, setExpandedQr] = useState<string | null>(null);

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    try {
      const prescRes = await api.get('/prescriptions/my');
      const prescList = prescRes.data;
      setPrescriptions(prescList);

      const remainingMap: Record<string, any[]> = {};
      await Promise.all(
        prescList.map(async (presc: any) => {
          try {
            const remainRes = await api.get(`/prescriptions/${presc.id}/remaining`);
            remainingMap[presc.id] = remainRes.data;
          } catch {
            remainingMap[presc.id] = [];
          }
        })
      );
      setRemaining(remainingMap);
    } catch (error) {
      console.error('Failed to fetch prescriptions');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color={Colors.primary} />
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.safeArea} edges={['top']}>
      <LinearGradient
        colors={[Colors.headerGradientStart, Colors.headerGradientEnd]}
        style={styles.header}
      >
        <Text style={styles.headerTitle}>Prescriptions</Text>
        <Text style={styles.headerSubtitle}>Present QR code at any pharmacy</Text>
      </LinearGradient>

      <ScrollView style={styles.container} contentContainerStyle={styles.content}>
        {prescriptions.length === 0 ? (
          <View style={styles.emptyState}>
            <View style={styles.emptyIcon}>
              <Ionicons name="document-text-outline" size={36} color={Colors.primary} />
            </View>
            <Text style={styles.emptyText}>No prescriptions yet</Text>
            <Text style={styles.emptySubtext}>
              Prescriptions appear here after a completed consultation
            </Text>
          </View>
        ) : (
          prescriptions.map(presc => {
            const remainingDrugs = remaining[presc.id] || [];
            const allDispensed = remainingDrugs.length === 0;

            return (
              <View key={presc.id} style={styles.prescriptionCard}>
                {/* Header */}
                <View style={styles.cardHeader}>
                  <View style={styles.rxBadge}>
                    <Text style={styles.rxText}>Rx</Text>
                  </View>
                  <View style={styles.cardInfo}>
                    <Text style={styles.prescId}>
                      #{presc.id.slice(0, 8).toUpperCase()}
                    </Text>
                    <Text style={styles.prescDate}>
                      {new Date(presc.issuedAt).toLocaleDateString('en-GB', {
                        day: 'numeric', month: 'short', year: 'numeric'
                      })}
                    </Text>
                  </View>
                  <View style={[
                    styles.statusBadge,
                    { backgroundColor: allDispensed ? Colors.successLight : Colors.warningLight }
                  ]}>
                    <Text style={[
                      styles.statusText,
                      { color: allDispensed ? Colors.success : Colors.warning }
                    ]}>
                      {allDispensed ? 'Complete' : 'Pending'}
                    </Text>
                  </View>
                </View>

                {/* Drugs */}
                <View style={styles.drugsSection}>
                  <Text style={styles.drugsTitle}>Prescribed Drugs</Text>
                  {presc.drugs.map((drug: string, index: number) => {
                    const isRemaining = remainingDrugs.some((r: any) => r.drugName === drug);
                    return (
                      <View key={index} style={styles.drugRow}>
                        <View style={styles.drugLeft}>
                          <Ionicons
                            name={isRemaining ? 'ellipse-outline' : 'checkmark-circle'}
                            size={18}
                            color={isRemaining ? Colors.textDisabled : Colors.success}
                          />
                          <Text style={styles.drugName}>{drug}</Text>
                        </View>
                        <View style={[
                          styles.drugBadge,
                          { backgroundColor: isRemaining ? Colors.warningLight : Colors.successLight }
                        ]}>
                          <Text style={[
                            styles.drugBadgeText,
                            { color: isRemaining ? Colors.warning : Colors.success }
                          ]}>
                            {isRemaining ? 'Pending' : 'Dispensed'}
                          </Text>
                        </View>
                      </View>
                    );
                  })}
                </View>

                {/* QR Code */}
                <TouchableOpacity
                  style={styles.qrButton}
                  onPress={() => setExpandedQr(expandedQr === presc.id ? null : presc.id)}
                >
                  <LinearGradient
                    colors={expandedQr === presc.id
                      ? [Colors.headerGradientStart, Colors.headerGradientEnd]
                      : [Colors.primaryLight, Colors.primaryLight]}
                    style={styles.qrButtonGradient}
                    start={{ x: 0, y: 0 }}
                    end={{ x: 1, y: 0 }}
                  >
                    <Ionicons
                      name="qr-code-outline"
                      size={18}
                      color={expandedQr === presc.id ? Colors.white : Colors.primary}
                    />
                    <Text style={[
                      styles.qrButtonText,
                      { color: expandedQr === presc.id ? Colors.white : Colors.primary }
                    ]}>
                      {expandedQr === presc.id ? 'Hide QR Code' : 'Show QR Code'}
                    </Text>
                  </LinearGradient>
                </TouchableOpacity>

                {expandedQr === presc.id && presc.qrCodeData && (
                  <View style={styles.qrContainer}>
                    <Image
                      source={{ uri: `data:image/png;base64,${presc.qrCodeData}` }}
                      style={styles.qrImage}
                      resizeMode="contain"
                    />
                    <Text style={styles.qrHint}>Present at any pharmacy to collect medication</Text>
                    {remainingDrugs.length > 0 && (
                      <View style={styles.remainingHint}>
                        <Ionicons name="information-circle-outline" size={14} color={Colors.warning} />
                        <Text style={styles.remainingHintText}>
                          {remainingDrugs.length} drug(s) still pending collection
                        </Text>
                      </View>
                    )}
                  </View>
                )}
              </View>
            );
          })
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: Colors.headerGradientStart },
  container: { flex: 1, backgroundColor: Colors.background },
  content: { padding: 20, paddingBottom: 40 },
  centered: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: Colors.background },
  header: { paddingHorizontal: 20, paddingTop: 16, paddingBottom: 20 },
  headerTitle: { fontSize: 22, fontWeight: '700', color: Colors.white },
  headerSubtitle: { fontSize: 13, color: 'rgba(255,255,255,0.75)', marginTop: 4 },
  emptyState: { alignItems: 'center', paddingVertical: 80 },
  emptyIcon: { width: 80, height: 80, borderRadius: 40, backgroundColor: Colors.primaryLight, justifyContent: 'center', alignItems: 'center', marginBottom: 16 },
  emptyText: { fontSize: 16, fontWeight: '600', color: Colors.textSecondary, marginBottom: 4 },
  emptySubtext: { fontSize: 13, color: Colors.textDisabled, textAlign: 'center', paddingHorizontal: 20 },
  prescriptionCard: { backgroundColor: Colors.surface, borderRadius: 16, padding: 18, marginBottom: 16, borderWidth: 1, borderColor: Colors.border },
  cardHeader: { flexDirection: 'row', alignItems: 'center', gap: 12, marginBottom: 16 },
  rxBadge: { width: 44, height: 44, borderRadius: 12, backgroundColor: Colors.primaryLight, justifyContent: 'center', alignItems: 'center' },
  rxText: { fontSize: 16, fontWeight: '800', color: Colors.primary, fontStyle: 'italic' },
  cardInfo: { flex: 1 },
  prescId: { fontSize: 15, fontWeight: '700', color: Colors.textPrimary },
  prescDate: { fontSize: 12, color: Colors.textSecondary, marginTop: 2 },
  statusBadge: { paddingHorizontal: 10, paddingVertical: 4, borderRadius: 20 },
  statusText: { fontSize: 12, fontWeight: '600' },
  drugsSection: { marginBottom: 16 },
  drugsTitle: { fontSize: 12, fontWeight: '600', color: Colors.textDisabled, textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 10 },
  drugRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: Colors.border },
  drugLeft: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  drugName: { fontSize: 14, color: Colors.textPrimary },
  drugBadge: { paddingHorizontal: 10, paddingVertical: 3, borderRadius: 20 },
  drugBadgeText: { fontSize: 11, fontWeight: '600' },
  qrButton: { borderRadius: 12, overflow: 'hidden' },
  qrButtonGradient: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, paddingVertical: 12 },
  qrButtonText: { fontSize: 14, fontWeight: '700' },
  qrContainer: { alignItems: 'center', paddingTop: 16 },
  qrImage: { width: 220, height: 220, marginBottom: 12 },
  qrHint: { fontSize: 13, color: Colors.textSecondary, textAlign: 'center' },
  remainingHint: { flexDirection: 'row', alignItems: 'center', gap: 6, marginTop: 8 },
  remainingHintText: { fontSize: 12, color: Colors.warning },
});